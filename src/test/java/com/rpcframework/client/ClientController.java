package com.rpcframework.client;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.service.DemoService;
import com.rpcframework.service.DemoServiceImpl;
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
		String msgId = demoService.hello();
	}

}
