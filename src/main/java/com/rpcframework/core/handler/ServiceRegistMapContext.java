package com.rpcframework.core.handler;

import com.rpcframework.monitor.ServiceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistMapContext {

	/**
	 * 客户端本地服务注册表
	 */
	private static final Map<String, List<ServiceModel>> rpcServiceMapping = new ConcurrentHashMap<>();

	public static ServiceModel getServiceModel(String serviceName) {
		return rpcServiceMapping.get(serviceName).get(0);
	}

	public static void addServiceModel(Map<String, List<ServiceModel>> map) {
		rpcServiceMapping.putAll(map);
	}

	public static List<ServiceModel> getServiceModels(String serviceName) {
		return rpcServiceMapping.get(serviceName);
	}

}
