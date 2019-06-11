package com.rpcframework.service;

import com.rpcframework.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wei.chen1
 * @since 2019/6/10
 */
@Component
public class SimpleFilter implements Filter{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
		logger.info("invoke {} end, and return is {} ",method.getName(),o);
	}

	@Override
	public void before(Method method, Object[] objects, Object o) throws Throwable {
		logger.info("invoke {} before, and paramter is {} ",method.getName(),objects);
	}
}
