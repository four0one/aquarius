package com.rpcframework.core.heartbeat;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
@ChannelHandler.Sharable
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	public HeartBeatClientHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("客户端连接激活");
		ctx.fireChannelActive();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.WRITER_IDLE) {
				logger.debug("客户端固定时间未写入数据，发送心跳消息");
				Ping ping = new Ping();
				ctx.writeAndFlush(ping);
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		String host = inetSocketAddress.getHostString();
		int port = inetSocketAddress.getPort();
		logger.debug("连接关闭.{}:{}", host, port);
		/*ReconnectProcessor processor = new ReconnectProcessor(host,port);
		processor.reconnect();*/
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Pong pong = (Pong) msg;
		logger.debug("心跳：{}", pong.getMsg());
	}
}
