package com.rpcframework.core;

import com.rpcframework.client.ClientController;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcClientBootstrapTest {

	public static void main(String[] args) {
		RpcClientBootstrap clientBootstrap = new RpcClientBootstrap("127.0.0.1",8089);
		clientBootstrap.start();
	}


}