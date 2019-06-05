package com.rpcframework.zookeeper;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public class CuratorConnect {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private CuratorFramework client;

	private String zkServer;

	private String workspace;

	private Map<String, InterProcessMutex> lockMap = new HashMap<>();

	public CuratorConnect(String zkServer, String workspace) {
		this.zkServer = zkServer;
		this.workspace = workspace;
	}

	public CuratorConnect(String config) {
		//读取classpath下的配置文件 文件名service_zk.properties
		Properties zkProperties = new Properties();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(config);
		try {
			zkProperties.load(inputStream);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		zkServer = zkProperties.getProperty("zk.url");
		workspace = zkProperties.getProperty("zk.root.node");
	}

	public void connect() {
		/**
		 * 同步创建zk示例，原生api是异步的
		 * 这一步是设置重连策略
		 *
		 * ExponentialBackoffRetry构造器参数：
		 *  curator链接zookeeper的策略:ExponentialBackoffRetry
		 *  baseSleepTimeMs：初始sleep的时间
		 *  maxRetries：最大重试次数
		 *  maxSleepMs：最大重试时间
		 */
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

		// 实例化Curator客户端，Curator的编程风格可以让我们使用方法链的形式完成客户端的实例化
		client = CuratorFrameworkFactory.builder() // 使用工厂类来建造客户端的实例对象
				.connectString(zkServer)  // 放入zookeeper服务器ip
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy)  // 设定会话时间以及重连策略
				.namespace(workspace).namespace("lock")
				.build();  // 建立连接通道
		// 启动Curator客户端
		client.start();
	}

	public boolean isConnected() {
		return client.getState().equals(CuratorFrameworkState.STARTED);
	}

	public void addNode(String path, String data, CreateMode createMode) {
		try {

			client.create().creatingParentsIfNeeded()
					.withMode(createMode)
					.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
					.forPath(path, StringUtils.isBlank(data) ? null : data.getBytes());
			logger.debug(path + "节点，创建成功...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean existNode(String path) {
		try {
			Stat stat = client.checkExists().forPath(path);
			return stat == null ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateNode(String path, String data, int version) {
		try {
			Stat resultStat = client.setData().withVersion(version)  // 指定数据版本
					.forPath(path, data.getBytes());  // 需要修改的节点路径以及新数据
			logger.debug("更新节点数据成功，新的数据版本为：" + resultStat.getVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NodeStat getNode(String path) {
		NodeStat nodeStat = new NodeStat();
		byte[] nodeData = new byte[0];
		try {
			nodeData = client.getData().storingStatIn(nodeStat.getStat()).forPath(path);
			nodeStat.setData(nodeData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeStat;
	}

	public void delNode(String path, int version) {
		try {
			client.delete()
					.guaranteed()  // 如果删除失败，那么在后端还是会继续删除，直到成功
					.deletingChildrenIfNeeded()  // 子节点也一并删除，也就是会递归删除
					.withVersion(version)
					.forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getNodeChildren(String path) {
		List<String> list = Lists.newArrayList();
		try {
			list = client.getChildren().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void listenNode(String path, CuratorWatcher curatorWatcher) {
		try {
			client.checkExists().usingWatcher(curatorWatcher).forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void listenChildrenChange(String path) {
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，可以设置触发的事件
		PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化
		 */
		try {
			childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 列出子节点数据列表，需要使用BUILD_INITIAL_CACHE同步初始化模式才能获得，异步是获取不到的
		List<ChildData> childDataList = childrenCache.getCurrentData();
		System.out.println("当前节点的子节点详细数据列表：");
		for (ChildData childData : childDataList) {
			logger.debug("\t* 子节点路径：" + new String(childData.getPath()) + "，该节点的数据为：" + new String(childData.getData()));
		}
		// 添加事件监听器
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
				// 通过判断event type的方式来实现不同事件的触发
				if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {  // 子节点初始化时触发
					System.out.println("\n--------------\n");
					System.out.println("子节点初始化成功");
				} else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {  // 添加子节点时触发
					System.out.println("\n--------------\n");
					System.out.print("子节点：" + event.getData().getPath() + " 添加成功，");
					System.out.println("该子节点的数据为：" + new String(event.getData().getData()));
				} else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {  // 删除子节点时触发
					System.out.println("\n--------------\n");
					System.out.println("子节点：" + event.getData().getPath() + " 删除成功");
				} else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {  // 修改子节点数据时触发
					System.out.println("\n--------------\n");
					System.out.print("子节点：" + event.getData().getPath() + " 数据更新成功，");
					System.out.println("子节点：" + event.getData().getPath() + " 新的数据为：" + new String(event.getData().getData()));
				}
			}
		});
	}

	// 关闭zk客户端连接
	private void closeZKClient() {
		if (client != null) {
			client.close();
		}
	}

	public void acquireLock(String lockName) throws Exception {
		client.usingNamespace("lock");
		InterProcessMutex lock = new InterProcessMutex(client, "/" + lockName);
		lock.acquire();
		lockMap.put(lockName, lock);
		client.usingNamespace(workspace);
	}

	public void releaseLock(String lockName) throws Exception {
		client.usingNamespace("lock");
		InterProcessMutex lock = lockMap.get(lockName);
		lock.release();
		client.usingNamespace(workspace);
	}

}
