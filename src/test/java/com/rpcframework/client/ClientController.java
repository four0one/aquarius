package com.rpcframework.client;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.core.codec.kryo.KryoFactoryPool;
import com.rpcframework.service.DemoService;
import com.rpcframework.service.DemoServiceImpl;
import com.rpcframework.service.Person;
import org.springframework.stereotype.Component;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
@Component
public class ClientController {

	@RpcResource
	private DemoService demoService;

	public void say(){
		String msg = demoService.hello();
		System.out.println(msg);
		Person person = demoService.findPerson("陈伟");
		System.out.println(person.getName());

	}

}
