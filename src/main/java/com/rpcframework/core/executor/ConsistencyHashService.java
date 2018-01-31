package com.rpcframework.core.executor;

import com.rpcframework.monitor.ServiceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author wei.chen1
 * @since 2018/1/25
 */
public class ConsistencyHashService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());


	//虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
	private SortedMap<Integer, VirtualServiceNode> virtualNodes;

	//虚拟节点的数目
	private final int VIRTUAL_NODES = 6;


	public ConsistencyHashService() {
	}

	public ConsistencyHashService(List<ServiceModel> serviceModes) {
		this.virtualNodes = new TreeMap<>();
		initServer(serviceModes);
	}

	public void initServer(List<ServiceModel> serviceModes) {

		for (ServiceModel realService : serviceModes) {
			for (int i = 0; i < VIRTUAL_NODES; i++) {
				VirtualServiceNode vsn = new VirtualServiceNode(realService, i);
				int hash = getHash(vsn.toHashString());
				logger.debug("虚拟节点[" + vsn + "]被添加, hash值为" + hash);
				virtualNodes.put(hash, vsn);
			}
		}
	}

	public ConsistencyHashRing generateHashRing(List<ServiceModel> serviceModes) {
		ConsistencyHashRing ring = new ConsistencyHashRing();
		VirtualServiceNode vsn;
		for (ServiceModel realService : serviceModes) {
			for (int i = 0; i < VIRTUAL_NODES; i++) {
				vsn = new VirtualServiceNode(realService, i);
				int hash = getHash(vsn.toHashString());
				logger.debug("虚拟节点[" + vsn + "]被添加, hash值为" + hash);
				ring.putVirtualNodes(hash, vsn);
			}
		}
		return ring;
	}

	//使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
	private int getHash(String str) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < str.length(); i++)
			hash = (hash ^ str.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;

		// 如果算出来的值为负数则取其绝对值
		if (hash < 0)
			hash = Math.abs(hash);
		return hash;
	}

	//得到应当路由到的结点
	public ServiceModel getServer(String key) {
		//得到该key的hash值
		int hash = getHash(key);
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, VirtualServiceNode> subMap = virtualNodes.tailMap(hash);
		VirtualServiceNode virtualNode;
		if (subMap.isEmpty()) {
			//如果没有比该key的hash值大的，则从第一个node开始
			Integer i = virtualNodes.firstKey();
			//返回对应的服务器
			virtualNode = virtualNodes.get(i);
		} else {
			//第一个Key就是顺时针过去离node最近的那个结点
			Integer i = subMap.firstKey();
			//返回对应的服务器
			virtualNode = subMap.get(i);
		}
		//virtualNode虚拟节点名称要截取一下
		if (virtualNode != null) {
			return virtualNode.getRealServiceNode();
		}
		return null;
	}

	//得到应当路由到的结点
	public ServiceModel getServer(String key,ConsistencyHashRing hashRing) {
		//得到该key的hash值
		int hash = getHash(key);
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, VirtualServiceNode> virtualNodes = hashRing.getVirtualNodes();
		SortedMap<Integer, VirtualServiceNode> subMap = virtualNodes.tailMap(hash);
		VirtualServiceNode virtualNode;
		if (subMap.isEmpty()) {
			//如果没有比该key的hash值大的，则从第一个node开始
			Integer i = virtualNodes.firstKey();
			//返回对应的服务器
			virtualNode = virtualNodes.get(i);
		} else {
			//第一个Key就是顺时针过去离node最近的那个结点
			Integer i = subMap.firstKey();
			//返回对应的服务器
			virtualNode = subMap.get(i);
		}
		//virtualNode虚拟节点名称要截取一下
		if (virtualNode != null) {
			return virtualNode.getRealServiceNode();
		}
		return null;
	}

	public static void main(String[] args) {
		List<ServiceModel> serviceModels = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ServiceModel sm = new ServiceModel();
			sm.setHost("127.0.0." + i);
			sm.setPort(8080 + i);
			serviceModels.add(sm);
		}
		ConsistencyHashService chs = new ConsistencyHashService(serviceModels);
		ServiceModel serviceModel1 = chs.getServer("DemoService#demo");
		System.out.println(serviceModel1.getHost() + ":" + serviceModel1.getPort());

		ServiceModel serviceModel2 = chs.getServer("HemoService#hello");
		System.out.println(serviceModel2.getHost() + ":" + serviceModel2.getPort());
	}


}
