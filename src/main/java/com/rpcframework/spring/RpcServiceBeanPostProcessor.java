package com.rpcframework.spring;

import com.rpcframework.annotation.RpcService;
import com.rpcframework.core.handler.RpcServiceContext;
import com.rpcframework.exception.RpcServiceBeansException;
import com.rpcframework.filter.Filter;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.register.ServiceRegister;
import com.rpcframework.register.ServiceRegisterFactory;
import com.rpcframework.utils.AopTargetUtils;
import com.rpcframework.utils.HttpUtils;
import com.rpcframework.utils.ServiceSignUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;


public class RpcServiceBeanPostProcessor extends ApplicationObjectSupport {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ServiceRegister serviceRegister;

	@Override
	protected void initApplicationContext(ApplicationContext context) throws BeansException {
		RpcRegistry registry = context.getBean(RpcRegistry.class);
		Monitor monitor = context.getBean(Monitor.class);
		serviceRegister = ServiceRegisterFactory.getServiceRegister(monitor);

		int port = registry.getPort();
		String host = null;
		try {
			host = InetAddress.getLocalHost().getHostAddress();//获得本机IP
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}

		if (StringUtils.isBlank(host)) {
			logger.error("本地ip获取失败");
			throw new RpcServiceBeansException("本地ip获取失败");
		}

		Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(RpcService.class);


		Set<String> keySet = beansWithAnnotation.keySet();
		if (keySet.isEmpty()) {
			return;
		}
		for (String beanName : keySet) {
			Object bean = beansWithAnnotation.get(beanName);
			Filter filter = null;
			RpcService rpcServiceAnno = AopTargetUtils.getTarget(bean).getClass().getAnnotation(RpcService.class);
			Class filterClass = rpcServiceAnno.filter();
			if (!filterClass.isAssignableFrom(Filter.NONE.class)) {
				filter = (Filter) context.getBean(filterClass);
			}
			//在这里生成并且put bean代理,后期可添加一些拦截器
			Object proxy = createServiceProxy(bean, filter);
			logger.debug("注册rpc方法");
			Class<?>[] interfaces = bean.getClass().getInterfaces();
			String serviceName = null;
			for (Class inf : interfaces) {
				if (inf.isAssignableFrom(Advised.class)) {
					continue;
				}
				Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(inf);
				if (null != allDeclaredMethods && allDeclaredMethods.length != 0) {
					//获取分布式锁，防止出现多个服务提供者同时注册
					serviceRegister.acquireLock(inf.getName());
					for (Method method : allDeclaredMethods) {
						logger.debug("interfaceName:{} methodName:{}", inf.getName(), method.getName());
						serviceName = ServiceSignUtils.sign(inf.getName(), method.getName());
						RpcServiceContext.addRpcMapping(serviceName, proxy);
						//注册服务信息
						registServiceToMonitor(serviceName, host, port);
					}
					serviceRegister.releaseLock(inf.getName());
				}
			}
		}

	}

	private void registServiceToMonitor(String serviceName, String host, int port) {
		ServiceModel serviceModel = new ServiceModel();
		serviceModel.setServiceName(serviceName);
		serviceModel.setHost(host);
		serviceModel.setPort(port);
		serviceModel.setAddress(host + ":" + port);
		serviceRegister.regist(serviceModel);
	}

	private Object createServiceProxy(Object bean, Filter filter) {
		ProxyFactory proxyFactory = new ProxyFactory(bean);
		if (null != filter) {
			RegexpMethodPointcutAdvisor advisor = new RegexpMethodPointcutAdvisor();
			advisor.setPattern(".*");
			advisor.setAdvice(filter);
			proxyFactory.addAdvisor(advisor);
		}
		return proxyFactory.getProxy();
	}
}