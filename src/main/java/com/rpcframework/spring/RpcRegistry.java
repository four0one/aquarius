package com.rpcframework.spring;

import com.rpcframework.core.RpcServerBootstrap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcRegistry implements InitializingBean, DisposableBean {

	private String address;

	private int port;

	private final static RpcServerBootstrap bootstrap = new RpcServerBootstrap();


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	@Override
	public void destroy() throws Exception {
		bootstrap.stop();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		bootstrap.start(port);
	}
}
