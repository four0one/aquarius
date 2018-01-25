package com.rpcframework.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcRequest implements Serializable {

	private String methodName;

	private String service;

	private Object[] parameters;

	private String requestId;

	private Class<?>[] parameterTypes;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	@Override
	public String toString() {
		return "RpcRequest{" +
				"methodName='" + methodName + '\'' +
				", service='" + service + '\'' +
				", parameters=" + Arrays.toString(parameters) +
				", requestId='" + requestId + '\'' +
				'}';
	}
}
