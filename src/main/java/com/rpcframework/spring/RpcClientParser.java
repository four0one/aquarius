package com.rpcframework.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 注入rpc引用注入器
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcClientParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ClientRpcServiceProxyProcessor.class);
		beanDefinition.setLazyInit(false);
		parserContext.getRegistry().registerBeanDefinition(ClientRpcServiceProxyProcessor.class.getName(),
				beanDefinition);
		return beanDefinition;
	}
}
