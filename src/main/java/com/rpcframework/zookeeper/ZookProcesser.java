package com.rpcframework.zookeeper;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.CreateMode.PERSISTENT;

/**
 * @author wei.chen1
 * @since 2018/12/20
 */
public class ZookProcesser implements Watcher {

	private ZooKeeper zooKeeper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private int status = 0;//连接状态 0 不能 1 能

	private CountDownLatch connectedSemaphore = new CountDownLatch(1);

	@Override
	public void process(WatchedEvent watchedEvent) {
		if (Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
			logger.info("zk connected!");
			status = Event.KeeperState.SyncConnected.getIntValue();
		} else {
			logger.info("zk not ready!");
		}
		connectedSemaphore.countDown();
	}

	public void init(String url) {
		try {
			zooKeeper = new ZooKeeper(url, 1500, this);
			connectedSemaphore.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public KeeperException.Code createNode(String path, String data, CreateMode createMode) {
		try {
			zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
			return KeeperException.Code.OK;
		} catch (KeeperException e) {
			logger.error(e.getMessage());
			return e.code();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return KeeperException.Code.SYSTEMERROR;
	}


	public void createRoot(String path, String data) {
		zooKeeper.create(path, data.getBytes(),
				ZooDefs.Ids.OPEN_ACL_UNSAFE, PERSISTENT, new AsyncCallback.StringCallback() {
					@Override
					public void processResult(int rc, String path, Object ctx, String name) {
						KeeperException.Code code = KeeperException.Code.get(rc);
						if (code == KeeperException.Code.OK) {
							logger.info("create root {} {} success!", path, name);
						}
					}
				}, data);
	}




	public void close() {
		try {
			zooKeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getStatus() {
		return status;
	}

	public static void main(String[] args) {
		ZookProcesser zookProcesser = new ZookProcesser();
		zookProcesser.init("127.0.0.1:2181");


		try {
			Thread.sleep(1000000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
