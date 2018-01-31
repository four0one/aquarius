package com.rpcframework.core;

import com.rpcframework.core.executor.ClientExecutor;
import com.rpcframework.core.executor.ClientExecutorContext;
import com.rpcframework.core.executor.HashExecutor;
import com.rpcframework.core.executor.SimpleExecutor;
import com.rpcframework.core.handler.ServiceRegistMapContext;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.utils.ServiceSignUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

	private final int LOCK_SIZE = 16;
	//锁分段
	private final List<ReentrantLock> transceiverLocks = new ArrayList<>(LOCK_SIZE);
	private final Map<String, Object> respMap = new ConcurrentHashMap();
	private final Map<String, Condition> conditionMap = new ConcurrentHashMap();

	private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

	public ClientTransceiver() {
		for (int i = 0; i < LOCK_SIZE; i++) {
			transceiverLocks.add(new ReentrantLock());
		}
	}

	private static class ClientTransceiverHolder {
		private static final ClientTransceiver INSTANCE = new ClientTransceiver();
	}

	public static ClientTransceiver getInstance() {
		return ClientTransceiverHolder.INSTANCE;
	}

	public RpcResponse sendRequest(RpcRequest rpcRequest) {
		requestAddId(rpcRequest);
		ReentrantLock transceiverLock = modAndGetLock(rpcRequest.getRequestId());
		transceiverLock.lock();
		Condition respArrive = transceiverLock.newCondition();
		try {
			ClientExecutor simpleExecutor = new SimpleExecutor();
			ClientExecutor hashExecutor = new HashExecutor(simpleExecutor);
			hashExecutor.execute(rpcRequest, new ClientExecutorContext());
			respMap.put(rpcRequest.getRequestId(), NoneObject.NONE);
			conditionMap.put(rpcRequest.getRequestId(), respArrive);
			respArrive.await();
			//接收到线程响应
			Object rpcResponse = respMap.get(rpcRequest.getRequestId());
			if (rpcResponse instanceof NoneObject) {
				return null;
			}
			respMap.remove(rpcRequest.getRequestId());
			conditionMap.remove(rpcRequest.getRequestId());
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
		if (response == null) {
			return;
		}
		RpcResponse rpcResponse = (RpcResponse) response;
		ReentrantLock transceiverLock = modAndGetLock(rpcResponse.getRequestId());
		transceiverLock.lock();
		try {
			String requestId = rpcResponse.getRequestId();
			//存在这次请求则写入返回，否则忽略
			if (respMap.containsKey(requestId)) {
				respMap.put(requestId, rpcResponse);
			}
			Condition respArrive = conditionMap.get(requestId);
			respArrive.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			transceiverLock.unlock();
		}
	}


	private ReentrantLock modAndGetLock(String requestId){
		int requestIdInt = Integer.parseInt(requestId.substring(13));
		int i = requestIdInt % LOCK_SIZE;
		return transceiverLocks.get(i);
	}
}
