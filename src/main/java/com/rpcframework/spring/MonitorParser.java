package com.rpcframework.spring;

import com.rpcframework.core.RpcClientBootstrap;
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
public class MonitorParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(Monitor.class);
		beanDefinition.setLazyInit(false);

		beanDefinition.getPropertyValues().addPropertyValue("config", element.getAttribute("config"));
		beanDefinition.getPropertyValues().addPropertyValue("strategy", element.getAttribute("strategy"));
		beanDefinition.getPropertyValues().addPropertyValue("flushInterval", element.getAttribute("flushInterval"));
		parserContext.getRegistry().registerBeanDefinition(Monitor.class.getName(),
				beanDefinition);
		return beanDefinition;
	}
}
