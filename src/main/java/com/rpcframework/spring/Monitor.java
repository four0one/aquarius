package com.rpcframework.spring;

/**
 * 监视器实体类
 * @author wei.chen1
 * @since 2018/1/24
 */
public class Monitor {

	private String config;

	private String strategy;

	private long flushInterval;

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public long getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(long flushInterval) {
		this.flushInterval = flushInterval;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
}
