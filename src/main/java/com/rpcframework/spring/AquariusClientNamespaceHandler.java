package com.rpcframework.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class AquariusClientNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("client", new RpcClientParser());
	}
}
