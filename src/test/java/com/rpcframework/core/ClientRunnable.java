package com.rpcframework.core;

import com.rpcframework.client.ClientController;

/**
 * @author wei.chen1
 * @since 2018/1/18
 */
public class ClientRunnable implements Runnable{

	private ClientController controller;

	public ClientRunnable(ClientController controller){
		this.controller = controller;
	}

	@Override
	public void run() {
		controller.say();
	}
}
