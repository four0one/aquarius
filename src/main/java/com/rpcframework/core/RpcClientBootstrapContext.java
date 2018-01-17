package com.rpcframework.core;


import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wei.chen1
 * @since 2018/1/17
 */
public class RpcClientBootstrapContext {

	private RpcClientBootstrap bootstrap;

	private Channel channel;


	private ScheduledFuture<?> scheduledFuture;

	private AtomicInteger restartCount = new AtomicInteger(3);

	private static class ContextHolder {
		private static RpcClientBootstrapContext INSTANCE = new RpcClientBootstrapContext();
	}

	public RpcClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(RpcClientBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public static RpcClientBootstrapContext getInstance() {
		return ContextHolder.INSTANCE;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public AtomicInteger getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(AtomicInteger restartCount) {
		this.restartCount = restartCount;
	}

	public ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}

	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}
}
