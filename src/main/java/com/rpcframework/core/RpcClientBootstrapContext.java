package com.rpcframework.core;


import com.rpcframework.core.pool.PooledChannelHolder;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wei.chen1
 * @since 2018/1/17
 */
public class RpcClientBootstrapContext {

	private Map<String, RpcClientBootstrap> bootstrapMap = new HashMap<>();

	private Map<String, List<Channel>> channelMap = new ConcurrentHashMap<>();
	private Map<String, PooledChannelHolder> pooledChannelHolderMap = new ConcurrentHashMap<>();

	private CopyOnWriteArrayList failureChannel = new CopyOnWriteArrayList();

	public void addBootstrap(String host, int port, RpcClientBootstrap rpcClientBootstrap) {
		bootstrapMap.put(hostAndPort(host, port), rpcClientBootstrap);
	}

	private static class ContextHolder {
		private static RpcClientBootstrapContext INSTANCE = new RpcClientBootstrapContext();
	}

	public RpcClientBootstrap getBootstrap(String host, int port) {
		return bootstrapMap.get(hostAndPort(host, port));
	}


	public static RpcClientBootstrapContext getInstance() {
		return ContextHolder.INSTANCE;
	}

	public Channel getChannel(String host, int port) {
		PooledChannelHolder holder = pooledChannelHolderMap.get(hostAndPort(host, port));
		return holder.popChannel();
	}

	public void removeChannel(String host, int port) {
		channelMap.remove(hostAndPort(host, port));
	}

	public void setChannel(Channel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
		String key = hostAndPort(socketAddress.getHostString(), socketAddress.getPort());
		if (channelMap.get(key) != null) {
			channelMap.get(key).add(channel);
		} else {
			List<Channel> list = new ArrayList<>();
			list.add(channel);
			channelMap.putIfAbsent(key, list);
		}
	}

	private String hostAndPort(String host, int port) {
		StringBuffer hostAndPort = new StringBuffer(host);
		hostAndPort.append(":");
		hostAndPort.append(port);
		return hostAndPort.toString();
	}

	public void addFailureChannel(String host, int port) {
		failureChannel.add(hostAndPort(host, port));
	}

	public CopyOnWriteArrayList<String> getFailureChannel() {
		return failureChannel;
	}

	public void addPooledChannelHolder(PooledChannelHolder pooledChannelHolder) {
		String key = hostAndPort(pooledChannelHolder.getHost(), pooledChannelHolder.getPort());
		this.pooledChannelHolderMap.putIfAbsent(key, pooledChannelHolder);
	}

	public PooledChannelHolder getPooledChannelHolder(String host, int port) {
		String key = hostAndPort(host, port);
		return this.pooledChannelHolderMap.get(key);
	}
}
