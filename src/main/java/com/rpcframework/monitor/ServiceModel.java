package com.rpcframework.monitor;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author wei.chen1
 * @since 2018/1/24
 */
public class ServiceModel implements Serializable{

	private String serviceName;

	private String address;

	private String host;

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceModel that = (ServiceModel) o;
		return port == that.port &&
				Objects.equal(serviceName, that.serviceName) &&
				Objects.equal(host, that.host);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serviceName, host, port);
	}
}
