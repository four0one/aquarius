package com.rpcframework.core.executor;

import com.rpcframework.core.RpcRequest;

/**
 * 客户端请求执行器
 * @author wei.chen1
 * @since 2018/1/26
 */
public interface ClientExecutor {

	void execute(RpcRequest rpcRequest, ClientExecutorContext ctx);
}
