package com.rpcframework.exception;

import org.springframework.beans.BeansException;

/**
 * @author wei.chen1
 * @since 2018/12/20
 */
public class ServiceRegException extends BeansException {
	public ServiceRegException(String msg) {
		super(msg);
	}
}
