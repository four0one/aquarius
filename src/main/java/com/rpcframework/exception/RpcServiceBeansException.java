package com.rpcframework.exception;

import org.springframework.beans.BeansException;

/**
 * @author wei.chen1
 * @since 2018/1/24
 */
public class RpcServiceBeansException extends BeansException {
	public RpcServiceBeansException(String msg) {
		super(msg);
	}

	public RpcServiceBeansException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
