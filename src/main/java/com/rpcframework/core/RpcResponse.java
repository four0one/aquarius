package com.rpcframework.core;

import java.io.Serializable;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcResponse implements Serializable {

	private String requestId;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "RpcResponse{" +
				"requestId='" + requestId + '\'' +
				'}';
	}
}
