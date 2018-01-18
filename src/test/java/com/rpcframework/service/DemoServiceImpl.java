package com.rpcframework.service;

import com.rpcframework.annotation.RpcService;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
@RpcService
public class DemoServiceImpl implements DemoService{


	@Override
	public String hello() {
		return "真的hello";
	}
}
