package com.rpcframework.spring;

import com.rpcframework.annotation.RpcService;
import com.rpcframework.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;


public class RpcServiceBeanPostProcessor extends ApplicationObjectSupport {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void initApplicationContext(ApplicationContext context) throws BeansException {
		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RpcService.class);
		beansWithAnnotation.keySet().forEach(beanName -> {
			Object bean = beansWithAnnotation.get(beanName);
			Filter filter = null;
			RpcService rpcServiceAnno = bean.getClass().getAnnotation(RpcService.class);
			Class filterClass = rpcServiceAnno.filter();
			if (!filterClass.isAssignableFrom(Filter.NONE.class)) {
				filter = (Filter) context.getBean(filterClass);
			}
			//在这里生成并且put bean代理
			Object proxy = createServiceProxy(bean, filter);
			logger.info("注册rpc方法");
			Class<?>[] interfaces = bean.getClass().getInterfaces();
			for (Class inf : interfaces) {
				Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(inf);
				for (Method method : allDeclaredMethods) {
					logger.info("interfaceName:{} methodName:{}", inf.getName(), method.getName());
					RpcServiceContext.addRpcMapping(inf.getName() + "#" + method.getName(), proxy);
				}
			}
		});
	}

	private Object createServiceProxy(Object bean, Filter filter) {
		ProxyFactory proxyFactory = new ProxyFactory(bean.getClass());
		RegexpMethodPointcutAdvisor advisor = new RegexpMethodPointcutAdvisor();
		advisor.setPattern(".*");
		advisor.setAdvice(filter);
		proxyFactory.addAdvisor(advisor);
		return proxyFactory.getProxy();
	}
}