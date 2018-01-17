package com.rpcframework.spring;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.exception.RpcRequestException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;


/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class ClientRpcServiceProxyProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		ClientRpcServiceProxy proxy = ClientRpcServiceProxy.getInstance();
		Class<?> cls = bean.getClass();
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(RpcResource.class)) {
				try {
					proxy.injectProxy(field, bean);
				} catch (RpcRequestException e) {
					throw e;
				}
			}
		}
		return bean;
	}
}
