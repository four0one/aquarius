package com.rpcframework.client;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.core.codec.kryo.KryoFactoryPool;
import com.rpcframework.service.DemoService;
import com.rpcframework.service.DemoServiceImpl;
import com.rpcframework.service.Person;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
@Component
public class ClientController {

	@RpcResource
	private DemoService demoService;

	private CountDownLatch runThreads;

	public void say(){
		String msg = demoService.hello();
		Person person = demoService.findPerson("陈伟");
		System.out.println(person);
		runThreads.countDown();

	}

    public CountDownLatch getRunThreads() {
        return runThreads;
    }

	public void setRunThreads(int ts) {
		runThreads = new CountDownLatch(ts);
	}
}
