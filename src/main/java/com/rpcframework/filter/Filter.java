package com.rpcframework.filter;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public interface Filter extends MethodBeforeAdvice, AfterReturningAdvice {

	/**
	 * 静态内部类，标志类（没有配置filter）
	 */
	class NONE{

	}

}
