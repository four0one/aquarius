package com.rpcframework.core.handler;

import com.rpcframework.core.executor.ConsistencyHashRing;
import com.rpcframework.monitor.ServiceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServiceRegistMapContext {

	/**
	 * 客户端本地服务注册表
	 */
	private static final Map<String, ConsistencyHashRing> rpcServiceHashRingMapping = new ConcurrentHashMap<>();

	private static final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

	public static void addRpcServiceHashRing(String serviceName, ConsistencyHashRing ring) {
		rwlock.writeLock().lock();
		rpcServiceHashRingMapping.put(serviceName, ring);
		rwlock.writeLock().unlock();
	}

	public static ConsistencyHashRing getServiceHashRing(String serviceName) {
		try {
			rwlock.readLock().lock();
			return rpcServiceHashRingMapping.get(serviceName);
		} finally {
			rwlock.readLock().unlock();
		}
	}

}
