package com.rpcframework.core.handler;

import com.rpcframework.monitor.ServiceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceRegistMapContext {

	/**
	 * 客户端本地服务注册表
	 */
	private static final Map<String, List<ServiceModel>> rpcServiceMapping = new ConcurrentHashMap<>();

	public static ServiceModel getServiceModel(String serviceName) {
		return rpcServiceMapping.get(serviceName).get(0);
	}

	public static void addServiceModel(String serviceName, CopyOnWriteArrayList list) {
		rpcServiceMapping.put(serviceName, list);
	}

	public static List<ServiceModel> getServiceModels(String serviceName) {
		return rpcServiceMapping.get(serviceName);
	}

}
