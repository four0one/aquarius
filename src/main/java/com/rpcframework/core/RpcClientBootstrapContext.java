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
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wei.chen1
 * @since 2018/1/17
 */
public class RpcClientBootstrapContext {

	private Map<String, RpcClientBootstrap> bootstrapMap = new HashMap<>();

	private Map<String, PooledChannelHolder> pooledChannelHolderMap = new HashMap<>();

	private final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

	public void addBootstrap(String host, int port, RpcClientBootstrap rpcClientBootstrap) {
		bootstrapMap.put(hostAndPort(host, port), rpcClientBootstrap);
	}

	private static class ContextHolder {
		private static RpcClientBootstrapContext INSTANCE = new RpcClientBootstrapContext();
	}

	public static RpcClientBootstrapContext getInstance() {
		return ContextHolder.INSTANCE;
	}

	public Channel getChannel(String host, int port) {
		PooledChannelHolder holder = pooledChannelHolderMap.get(hostAndPort(host, port));
		return holder.popChannel();
	}

	private String hostAndPort(String host, int port) {
		StringBuffer hostAndPort = new StringBuffer(host);
		hostAndPort.append(":");
		hostAndPort.append(port);
		return hostAndPort.toString();
	}

	public void addPooledChannelHolder(PooledChannelHolder pooledChannelHolder) {
		rwlock.writeLock().lock();
		String key = hostAndPort(pooledChannelHolder.getHost(), pooledChannelHolder.getPort());
		this.pooledChannelHolderMap.putIfAbsent(key, pooledChannelHolder);
		rwlock.writeLock().unlock();
	}

	public PooledChannelHolder getPooledChannelHolder(String host, int port) {
		try {
			rwlock.readLock().lock();
			String key = hostAndPort(host, port);
			return this.pooledChannelHolderMap.get(key);
		} finally {
			rwlock.readLock().unlock();
		}
	}

	public void removePooledChannelHolder(String host,int port) {
		String key = hostAndPort(host, port);
		this.pooledChannelHolderMap.remove(key);
	}
}
