package com.rpcframework.service;

import com.rpcframework.annotation.RpcService;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
@RpcService
public class DemoServiceImpl implements DemoService{


	@Override
	public void hello() {
		System.out.println("hello world");
	}
}
