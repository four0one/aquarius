package com.rpcframework.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpcframework.core.RpcClientBootstrap;
import com.rpcframework.core.handler.ServiceRegistMapContext;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.utils.HttpUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 客户端监视器实体类
 * 加载完成之后在afterPropertiesSet方法内初始化服务注册表，通道预加载
 *
 * @author wei.chen1
 * @since 2018/1/24
 */
public class ClientMonitor implements InitializingBean {

	private String address;

	private long flushInterval;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(long flushInterval) {
		this.flushInterval = flushInterval;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		HttpUtils httpUtils = HttpUtils.getInstance();
		String result = httpUtils.getJson(address+"/services/load");
		Map<String,List<ServiceModel>> serviceMap = mapper.readValue(result,new TypeReference<Map<String,List<ServiceModel>>>(){});
		if (null == serviceMap || serviceMap.isEmpty()) {
			throw new Exception("客户端同步服务注册表信息为空");
		}

		//去重服务器地址
		Set<ServiceModel> serviceModelSet = new HashSet<>();
		Set<String> keySet = serviceMap.keySet();
		CopyOnWriteArrayList copyList = null;
		for (String key : keySet) {
			copyList = new CopyOnWriteArrayList();
			List<ServiceModel> serviceModels = serviceMap.get(key);
			for (ServiceModel model : serviceModels) {
				serviceModelSet.add(model);
				copyList.add(model);
			}
			ServiceRegistMapContext.addServiceModel(key,copyList);
		}

		//打开channel
		openServiceChannel(serviceModelSet);

	}

	private void openServiceChannel(Set<ServiceModel> serviceModelSet) {
		RpcClientBootstrap clientBootstrap = null;
		for (ServiceModel model : serviceModelSet) {
			clientBootstrap = new RpcClientBootstrap(model.getHost(), model.getPort());
			clientBootstrap.start();
		}
	}
}
