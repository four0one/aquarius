package com.rpcframework.core;


import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wei.chen1
 * @since 2018/1/17
 */
public class RpcClientBootstrapContext {

	private RpcClientBootstrap bootstrap;

	private Channel channel;
	private Map<String, Channel> channelMap = new HashMap<>();


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

	public Channel getChannel(String host, int port) {
		StringBuffer hostAndPort = hostAndPort(host, port);
		return channelMap.get(hostAndPort.toString());
	}

	public void setChannel(Channel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
		StringBuffer hostAndPort = hostAndPort(socketAddress.getHostString(), socketAddress.getPort());
		channelMap.put(hostAndPort.toString(), channel);
	}

	private StringBuffer hostAndPort(String host, int port) {
		StringBuffer hostAndPort = new StringBuffer(host);
		hostAndPort.append(":");
		hostAndPort.append(port);
		return hostAndPort;
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
