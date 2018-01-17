package com.rpcframework.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServiceContext {

	private static final Map<String, Object> rpcServiceMapping = new ConcurrentHashMap<>();

	public static void addRpcMapping(String methodSign, Object target) {
		rpcServiceMapping.put(methodSign, target);
	}

	public static Object getRpcTarget(String methodSign) {
		return rpcServiceMapping.get(methodSign);
	}

}
