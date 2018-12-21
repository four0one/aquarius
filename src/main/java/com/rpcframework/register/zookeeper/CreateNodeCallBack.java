package com.rpcframework.register.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/12/20
 */
public class CreateNodeCallBack implements AsyncCallback.StringCallback {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		KeeperException.Code code = KeeperException.Code.get(rc);
		if (code == KeeperException.Code.OK || code == KeeperException.Code.NODEEXISTS) {
			logger.info("node {} create", path);
		}

		if (code == KeeperException.Code.CONNECTIONLOSS) {
			logger.info("register service {} connloss,try again", path);
		}
	}
}
