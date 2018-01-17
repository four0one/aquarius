package com.rpcframework.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 *	解析服务端配置的rpcservice实例
 */
public class RpcServerParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(RpcServiceBeanPostProcessor.class);
		beanDefinition.setLazyInit(false);
		parserContext.getRegistry().registerBeanDefinition(RpcServiceBeanPostProcessor.class.getName(),
				beanDefinition);

		return beanDefinition;
	}
}
