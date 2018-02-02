package com.rpcframework.core.executor;

import com.rpcframework.core.RpcClientBootstrapContext;
import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.SnowflakeIdWorker;
import com.rpcframework.core.handler.ServiceRegistMapContext;
import com.rpcframework.core.pool.PooledChannel;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.utils.ServiceSignUtils;
import io.netty.channel.Channel;

import java.util.List;

/**
 * hash均衡执行
 *
 * @author wei.chen1
 * @since 2018/1/26
 */
public class HashExecutor implements ClientExecutor {

	private ClientExecutor simpleExecutor;

	private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);

	private ConsistencyHashService hashService = new ConsistencyHashService();

	public HashExecutor(ClientExecutor simpleExecutor) {
		this.simpleExecutor = simpleExecutor;
	}

	@Override
	public void execute(RpcRequest rpcRequest, ClientExecutorContext ctx) {
		String serviceName = ServiceSignUtils.sign(rpcRequest.getService(), rpcRequest.getMethodName());
		PooledChannel channel = requestHashChannel(rpcRequest, serviceName);
		if (channel == null) {
			return;
		}
		ctx.setChannel(channel);
		simpleExecutor.execute(rpcRequest, ctx);
	}

	private PooledChannel requestHashChannel(RpcRequest rpcRequest, String serviceName) {
		PooledChannel channel = null;
		ConsistencyHashRing serviceHashRing = ServiceRegistMapContext.getServiceHashRing(serviceName);
		do {
			ServiceModel serviceModel = hashService.getServer(rpcRequest.toString(), serviceHashRing);
			channel = RpcClientBootstrapContext.getInstance().getChannel(serviceModel.getHost(), serviceModel.getPort());
			if (null == channel) {
				List<ServiceModel> serviceModels = ServiceRegistMapContext.getServiceModels(serviceName);
				serviceModels.remove(serviceModel);
				serviceHashRing = hashService.generateHashRing(serviceModels);
				ServiceRegistMapContext.addRpcServiceHashRing(serviceName, serviceHashRing);
			}
		} while (channel == null && serviceHashRing.getVirtualNodes().size() != 0);
		return channel;
	}

}
