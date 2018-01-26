package com.rpcframework.core.executor;

import com.rpcframework.core.RpcClientBootstrapContext;
import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.SnowflakeIdWorker;
import com.rpcframework.core.handler.ServiceRegistMapContext;
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

	public HashExecutor(ClientExecutor simpleExecutor) {
		this.simpleExecutor = simpleExecutor;
	}

	@Override
	public void execute(RpcRequest rpcRequest, ClientExecutorContext ctx) {
		requestAddId(rpcRequest);
		List<ServiceModel> serviceModels = ServiceRegistMapContext.getServiceModels(
				ServiceSignUtils.sign(rpcRequest.getService(), rpcRequest.getMethodName())
		);
		ConsistencyHashService hashService = new ConsistencyHashService(serviceModels);
		ServiceModel serviceModel = hashService.getServer(rpcRequest.toString());
		mappingChannel(ctx, serviceModel);
		simpleExecutor.execute(rpcRequest, ctx);
	}

	private void mappingChannel(ClientExecutorContext ctx, ServiceModel serviceModel) {
		Channel channel = RpcClientBootstrapContext.getInstance().getChannel(serviceModel.getHost(), serviceModel.getPort());
		ctx.setChannel(channel);
	}


	private void requestAddId(RpcRequest rpcRequest) {
		long id = idWorker.nextId();
		String requestId = String.valueOf(id);
		rpcRequest.setRequestId(requestId);
	}

}
