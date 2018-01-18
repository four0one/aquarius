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
public class RpcClientParser implements BeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		//TODO 暂时先把客户端连接启动放在这边
		RpcClientBootstrap clientBootstrap = new RpcClientBootstrap("127.0.0.1",8089);
		clientBootstrap.start();

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(ClientRpcServiceProxyProcessor.class);
		beanDefinition.setLazyInit(false);
		parserContext.getRegistry().registerBeanDefinition(ClientRpcServiceProxyProcessor.class.getName(),
				beanDefinition);
		return beanDefinition;
	}
}
