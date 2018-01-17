package com.rpcframework.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcRequest implements Serializable {

	private String method;

	private String service;

	private Object[] parameters;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	@Override
	public String toString() {
		return "RpcRequest{" +
				"method='" + method + '\'' +
				", service='" + service + '\'' +
				", parameters=" + Arrays.toString(parameters) +
				'}';
	}
}
