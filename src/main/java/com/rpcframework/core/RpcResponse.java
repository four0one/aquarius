package com.rpcframework.core;

import java.io.Serializable;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcResponse implements Serializable {

	private String requestId;

	private Object result;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "RpcResponse{" +
				"requestId='" + requestId + '\'' +
				'}';
	}
}
