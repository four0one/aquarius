package com.rpcframework.core.handler;

import com.rpcframework.core.RpcClientBootstrapContext;
import com.rpcframework.core.RpcRequest;
import com.rpcframework.core.heartbeat.Pong;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.debug(" 收到返回 : {}", msg);
		if (msg instanceof Pong) {
			ctx.fireChannelRead(msg);
			return;
		}
		//TODO 处理服务调用，线程or调用
	}

	public void sendRequest(RpcRequest rpcRequest) {
		Channel channel = RpcClientBootstrapContext.getInstance().getChannel();
		channel.writeAndFlush(rpcRequest);
	}
}
