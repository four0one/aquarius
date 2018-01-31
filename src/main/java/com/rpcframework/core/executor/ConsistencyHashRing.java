package com.rpcframework.core.executor;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author wei.chen1
 * @since 2018/1/31
 */
public class ConsistencyHashRing {

	//虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
	private SortedMap<Integer, VirtualServiceNode> virtualNodes;


	public ConsistencyHashRing() {
		this.virtualNodes = new TreeMap<>();
	}

	public SortedMap<Integer, VirtualServiceNode> getVirtualNodes() {
		return virtualNodes;
	}

	public void putVirtualNodes(int hash, VirtualServiceNode node) {
		this.virtualNodes.put(hash, node);
	}


}
