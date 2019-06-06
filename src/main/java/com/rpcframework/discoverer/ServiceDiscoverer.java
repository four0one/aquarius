package com.rpcframework.discoverer;

import com.rpcframework.core.executor.ConsistencyHashRing;
import com.rpcframework.monitor.ServiceModel;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.List;
import java.util.Map;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public interface ServiceDiscoverer {

	Map<String, List<ServiceModel>> find();

	/**
	 * 根据服务名称生成对应的节点哈希环
	 *
	 * @param serviceName
	 * @return
	 */
	ConsistencyHashRing generateServiceRing(String serviceName);

	void listenService(String serviceName, PathChildrenCacheListener listener);

}
