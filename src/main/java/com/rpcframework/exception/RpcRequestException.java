package com.rpcframework.exception;

import org.springframework.beans.BeansException;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcRequestException extends BeansException {

	public RpcRequestException(String desc){
		super(desc);
	}

}
