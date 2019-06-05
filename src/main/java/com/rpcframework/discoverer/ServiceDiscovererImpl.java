package com.rpcframework.discoverer;

import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.zookeeper.CuratorConnect;
import com.rpcframework.zookeeper.NodeStat;
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
			NodeStat node = null;
			for (String serviceName : serviceList) {
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
				map.put(serviceName, serviceModelList);
			}
		}
		return map;
	}

}
