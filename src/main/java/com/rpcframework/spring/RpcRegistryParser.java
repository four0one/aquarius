package com.rpcframework.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcRegistryParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(RpcRegistry.class);
		beanDefinition.setLazyInit(false);
		beanDefinition.getPropertyValues().addPropertyValue("address", element.getAttribute("address"));
		beanDefinition.getPropertyValues().addPropertyValue("port", element.getAttribute("port"));
		parserContext.getRegistry().registerBeanDefinition(RpcRegistry.class.getName(),
				beanDefinition);
		return beanDefinition;
	}
}
