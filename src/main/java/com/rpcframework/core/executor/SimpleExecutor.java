package com.rpcframework.core.executor;

import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.pool.PooledChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/1/26
 */
public class SimpleExecutor implements ClientExecutor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(RpcRequest rpcRequest, ClientExecutorContext ctx) {
		try {
			PooledChannel channel = ctx.getChannel();
			channel.writeAndFlush(rpcRequest);
			logger.debug("request:{}", rpcRequest);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


}
