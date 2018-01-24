package com.rpcframework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AquariusNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("registry", new RpcRegistryParser());
		registerBeanDefinitionParser("monitor", new MonitorParser());
		registerBeanDefinitionParser("server", new RpcServerParser());
	}
}
