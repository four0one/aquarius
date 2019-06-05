package com.rpcframework.register;

import com.rpcframework.monitor.ServiceModel;

/**
 * @author wei.chen1
 * @since 2018/6/6
 */
public interface ServiceRegister {

	void regist(ServiceModel serviceModel);
	void acquireLock(String interfaceName);
	void releaseLock(String interfaceName);
}
