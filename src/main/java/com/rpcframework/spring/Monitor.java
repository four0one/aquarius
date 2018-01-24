package com.rpcframework.spring;

/**
 * 监视器实体类
 * @author wei.chen1
 * @since 2018/1/24
 */
public class Monitor {

	private String address;

	private long flushInterval;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(long flushInterval) {
		this.flushInterval = flushInterval;
	}
}
