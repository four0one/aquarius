package com.rpcframework.register.impl;

import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.register.ServiceRegister;
import com.rpcframework.register.zookeeper.ZookProcesser;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
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

	private String zookUrl;

	private String rootNode;

	private ZookProcesser zookProcesser;

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
		//读取classpath下的配置文件 文件名service_zk.properties
		Properties zkProperties = new Properties();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(config);
		try {
			zkProperties.load(inputStream);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		zookUrl = zkProperties.getProperty("zk.url");
		rootNode = zkProperties.getProperty("zk.root.node");

		zookProcesser = new ZookProcesser();
		zookProcesser.init(zookUrl);
		if (zookProcesser.getStatus() == 1) {
			zookProcesser.createRoot(rootNode, "app root");
		}
	}

	@Override
	public void regist(ServiceModel serviceModel) {
		if (zookProcesser.getStatus() != 1) {
			logger.warn("service {} regist fail", serviceModel.getServiceName());
			return;
		}
		KeeperException.Code code = zookProcesser.createNode(rootNode + "/" + serviceModel.getServiceName(), "", CreateMode.PERSISTENT);
		if (code.equals(KeeperException.Code.OK) || code.equals(KeeperException.Code.NODEEXISTS)) {
			logger.debug("service {} regist success", serviceModel.getServiceName());
			zookProcesser.createNode(rootNode + "/" +
					serviceModel.getServiceName() + "/node-", serviceModel.getAddress(), CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("service node {} regist", serviceModel.getAddress());
		}
	}

}
