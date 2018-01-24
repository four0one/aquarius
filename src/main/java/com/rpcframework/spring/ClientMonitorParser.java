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
public class ClientMonitorParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ClientMonitor.class);
		beanDefinition.setLazyInit(false);

		beanDefinition.getPropertyValues().addPropertyValue("address", element.getAttribute("address"));
		beanDefinition.getPropertyValues().addPropertyValue("flushInterval", element.getAttribute("flushInterval"));
		parserContext.getRegistry().registerBeanDefinition(ClientMonitor.class.getName(),
				beanDefinition);
		return beanDefinition;
	}
}
