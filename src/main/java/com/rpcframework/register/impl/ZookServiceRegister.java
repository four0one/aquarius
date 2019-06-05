package com.rpcframework.register.impl;

import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.register.ServiceRegister;
import com.rpcframework.zookeeper.CuratorConnect;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wei.chen1
 * @since 2018/12/20
 */
public class ZookServiceRegister implements ServiceRegister {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CuratorConnect curatorConnect;

	private static ZookServiceRegister ourInstance;

	public static ZookServiceRegister getInstance() {
		ourInstance = new ZookServiceRegister("service_zk.properties");
		return ourInstance;
	}

	public static ZookServiceRegister getInstance(String config) {
		if (StringUtils.isBlank(config)) {
			ourInstance = new ZookServiceRegister("service_zk.properties");
		} else {
			ourInstance = new ZookServiceRegister(config);
		}
		return ourInstance;
	}

	private ZookServiceRegister(String config) {
		curatorConnect = new CuratorConnect(config);
		curatorConnect.connect();
	}

	@Override
	public void regist(ServiceModel serviceModel) {
		String path = "/" + serviceModel.getServiceName();
		if (!curatorConnect.existNode(path)) {
			curatorConnect.addNode(path, null, CreateMode.PERSISTENT);
		}
		curatorConnect.addNode(path + "/node-", serviceModel.getAddress(), CreateMode.EPHEMERAL_SEQUENTIAL);
	}

	@Override
	public void acquireLock(String interfaceName) {
		try {
			curatorConnect.acquireLock(interfaceName);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void releaseLock(String interfaceName) {
		try {
			curatorConnect.releaseLock(interfaceName);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
