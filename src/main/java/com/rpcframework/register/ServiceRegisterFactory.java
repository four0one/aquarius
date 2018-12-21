package com.rpcframework.register;

import com.rpcframework.register.impl.ZookServiceRegister;
import com.rpcframework.spring.Monitor;

/**
 * @author wei.chen1
 * @since 2018/12/21
 */
public class ServiceRegisterFactory {

	private final static String STRATEGY_ZK = "zk";

	public static ServiceRegister getServiceRegister(Monitor monitor){
		if(STRATEGY_ZK.equals(monitor.getStrategy())){
			return ZookServiceRegister.getInstance(monitor.getConfig());
		}
		return ZookServiceRegister.getInstance(monitor.getConfig());
	}

}
