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

	@Override
	public Person findPerson(String name) {
		Person person = new Person();
		person.setId(18);
		person.setName(name);
		person.addAddress("江苏 南京");
		person.addAddress("上海 上海");
		return person;
	}
}
