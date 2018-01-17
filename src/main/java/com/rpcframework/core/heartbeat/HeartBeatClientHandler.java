package com.rpcframework.core.heartbeat;

import com.rpcframework.core.RpcClientBootstrapContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("客户端连接激活");
		RpcClientBootstrapContext.getInstance().setChannel(ctx.channel());
		RpcClientBootstrapContext.getInstance().setRestartCount(new AtomicInteger(3));
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
		logger.debug("连接关闭，将进行重连");
		RpcClientBootstrapContext context = RpcClientBootstrapContext.getInstance();
		ScheduledFuture<?> scheduledFuture = ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.debug("重连...{}", context.getRestartCount().get());
				context.getBootstrap().restart();
			}
		}, 0, 60, TimeUnit.SECONDS);
		context.setScheduledFuture(scheduledFuture);

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Pong pong = (Pong) msg;
		logger.debug("心跳：{}", pong.getMsg());
	}
}
