package com.rpcframework.zookeeper;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public class ServiceWatcher implements CuratorWatcher {

	@Override
	public void process(WatchedEvent watchedEvent) throws Exception {
		System.out.println("触发watcher，节点路径为：" + watchedEvent.getPath());
		System.out.println("事件为：" + watchedEvent.getType().name());
	}
}
