package com.rpcframework.core;

import com.rpcframework.core.handler.RpcClientContext;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.utils.ServiceSignUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户端收发器，发送和接受请求结果，使用重入锁保持线程同步不阻塞执行
 * 单例
 *
 * @author wei.chen1
 * @since 2018/1/18
 */
public class ClientTransceiver {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final ReentrantLock transceiverLock = new ReentrantLock();
	private final Condition respArrive = transceiverLock.newCondition();
	private final Map<String, Object> respMap = new ConcurrentHashMap();

	//workerid和dcid要配置
	private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

	private static class ClientTransceiverHolder {
		private static final ClientTransceiver INSTANCE = new ClientTransceiver();
	}

	public static ClientTransceiver getInstance() {
		return ClientTransceiverHolder.INSTANCE;
	}

	public RpcResponse sendRequest(RpcRequest rpcRequest) {
		transceiverLock.lock();
		try {
			requestAddId(rpcRequest);
			//获取接口的服务器地址
			ServiceModel serviceModel = RpcClientContext.getServiceModel(
					ServiceSignUtils.sign(rpcRequest.getService(),rpcRequest.getMethodName())
			);
			Channel channel = RpcClientBootstrapContext.getInstance().getChannel(serviceModel.getHost(),serviceModel.getPort());
			channel.writeAndFlush(rpcRequest);
			logger.debug("request:{}",rpcRequest);
			respMap.put(rpcRequest.getRequestId(), NoneObject.NONE);
			respArrive.await();
			//接收到线程响应
			Object rpcResponse = respMap.get(rpcRequest.getRequestId());
			if (rpcResponse instanceof NoneObject) {
				return null;
			}
			respMap.remove(rpcRequest.getRequestId());
			return (RpcResponse) rpcResponse;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			transceiverLock.unlock();
		}
		return null;

	}

	private void requestAddId(RpcRequest rpcRequest) {
		long id = idWorker.nextId();
		String requestId = String.valueOf(id);
		rpcRequest.setRequestId(requestId);
	}

	public void recvResponse(Object response) {
		transceiverLock.lock();
		try {
			if (response == null) {
				return;
			}
			RpcResponse rpcResponse = (RpcResponse) response;
			String requestId = rpcResponse.getRequestId();
			//存在这次请求则写入返回，否则忽略
			if (respMap.containsKey(requestId)) {
				respMap.put(requestId, rpcResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			respArrive.signal();
			transceiverLock.unlock();
		}
	}

}
