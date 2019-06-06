package com.rpcframework.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcServerBootstrapTest2 {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-config-study01.xml");
	}

}