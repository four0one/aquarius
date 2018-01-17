package com.rpcframework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AquariusNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("server", new RpcServerParser());
		registerBeanDefinitionParser("registry", new RpcRegistryParser());
	}
}
