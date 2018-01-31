package com.rpcframework.core.executor;

import com.rpcframework.monitor.ServiceModel;

/**
 * @author wei.chen1
 * @since 2018/1/31
 */
public class VirtualServiceNode {

	private ServiceModel realServiceNode;

	private String virtualNode = "vn";

	private int i;

	public VirtualServiceNode(ServiceModel realServiceNode, int i) {
		this.realServiceNode = realServiceNode;
		this.i = i;
	}

	public ServiceModel getRealServiceNode() {
		return realServiceNode;
	}

	public void setRealServiceNode(ServiceModel realServiceNode) {
		this.realServiceNode = realServiceNode;
	}

	public String toHashString() {
		return "VirtualServiceNode{" +
				"realServiceNode=" + realServiceNode.toHashString() +
				", virtualNode='" + virtualNode + '\'' +
				", i=" + i +
				'}';
	}

	@Override
	public String toString() {
		return "VirtualServiceNode{" +
				"realServiceNode=" + realServiceNode +
				", virtualNode='" + virtualNode + '\'' +
				", i=" + i +
				'}';
	}
}
