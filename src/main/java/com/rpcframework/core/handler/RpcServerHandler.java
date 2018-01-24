package com.rpcframework.core.handler;

import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.RpcResponse;
import com.rpcframework.core.heartbeat.Ping;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端接受请求处理器
 *
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Ping) {
			ctx.fireChannelRead(msg);
			return;
		}
		if (msg instanceof RpcRequest) {
			logger.debug("request:{}", msg);
			RpcRequest request = (RpcRequest) msg;
			RpcResponse response = new RpcResponse();
			response.setRequestId(request.getRequestId());
			ctx.writeAndFlush(response);
			return;
		}
	}

}
