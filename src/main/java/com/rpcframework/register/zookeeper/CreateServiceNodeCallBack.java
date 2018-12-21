package com.rpcframework.register.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/12/21
 */
public class CreateServiceNodeCallBack implements AsyncCallback.StringCallback {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String address;

	private String currentPath;

	private ZookProcesser zookProcesser;

	public CreateServiceNodeCallBack(String address, String currentPath, ZookProcesser zookProcesser) {
		this.address = address;
		this.currentPath = currentPath;
		this.zookProcesser = zookProcesser;
	}

	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		KeeperException.Code code = KeeperException.Code.get(rc);
		if (code == KeeperException.Code.OK || code == KeeperException.Code.NODEEXISTS) {
			logger.info("service {} create", path);
			if (currentPath.equals(path)) {
			}
		}

		if (code == KeeperException.Code.CONNECTIONLOSS) {
			logger.info("register service {} connloss,try again", path);
			return;
		}
	}
}
