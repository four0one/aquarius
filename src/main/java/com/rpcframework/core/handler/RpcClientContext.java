package com.rpcframework.core.handler;

import com.rpcframework.monitor.ServiceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientContext {

	private static final Map<String, List<ServiceModel>> rpcServiceMapping = new HashMap<>();

	public static ServiceModel getServiceModel(String serviceName){
		return rpcServiceMapping.get(serviceName).get(0);
	}

	public static void addServiceModel(Map<String,List<ServiceModel>> map){
		rpcServiceMapping.putAll(map);
	}

}
