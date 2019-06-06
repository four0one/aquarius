package com.rpcframework.discoverer;

import com.rpcframework.core.executor.ConsistencyHashRing;
import com.rpcframework.core.executor.ConsistencyHashService;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.zookeeper.CuratorConnect;
import com.rpcframework.zookeeper.NodeStat;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wei.chen1
 * @since 2019/6/4
 */
public class ServiceDiscovererImpl implements ServiceDiscoverer {

	private CuratorConnect curatorConnect;

	public ServiceDiscovererImpl(String config) {
		curatorConnect = new CuratorConnect(config);
		curatorConnect.connect();
	}


	@Override
	public Map<String, List<ServiceModel>> find() {
		Map<String, List<ServiceModel>> map = new HashMap<>();
		List<String> serviceList = curatorConnect.getNodeChildren("/");
		if (!CollectionUtils.isEmpty(serviceList)) {
			for (String serviceName : serviceList) {
				List<ServiceModel> serviceModelList = getServiceModelList(serviceName);
				map.put(serviceName, serviceModelList);
			}
		}
		return map;
	}

	private List<ServiceModel> getServiceModelList(String serviceName) {
		NodeStat node;
		List<String> serviceNodeList = curatorConnect.getNodeChildren("/" + serviceName);
		List<ServiceModel> serviceModelList = new ArrayList<>();
		ServiceModel serviceModel = null;
		for (String nodeName : serviceNodeList) {
			node = curatorConnect.getNode("/" + serviceName + "/" + nodeName);
			serviceModel = new ServiceModel();
			String data = node.getData();
			String[] hostAndPort = data.split(":");
			serviceModel.setAddress(data);
			serviceModel.setServiceName(serviceName);
			serviceModel.setPort(Integer.parseInt(hostAndPort[1]));
			serviceModel.setHost(hostAndPort[0]);
			serviceModelList.add(serviceModel);
		}
		return serviceModelList;
	}

	@Override
	public ConsistencyHashRing generateServiceRing(String serviceName) {
		List<ServiceModel> serviceModelList = getServiceModelList(serviceName);
		ConsistencyHashService hashService = new ConsistencyHashService();
		ConsistencyHashRing hashRing = hashService.generateHashRing(serviceModelList);
		return hashRing;
	}

	@Override
	public void listenService(String serviceName, PathChildrenCacheListener listener) {
		curatorConnect.listenChildrenChange("/" + serviceName, listener);
	}
}
