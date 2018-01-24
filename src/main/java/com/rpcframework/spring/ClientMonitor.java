package com.rpcframework.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpcframework.core.RpcClientBootstrap;
import com.rpcframework.core.handler.RpcClientContext;
import com.rpcframework.monitor.ServiceModel;
import com.rpcframework.utils.HttpUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;


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

		//打开channel
		Set<ServiceModel> serviceModelSet = new HashSet<>();
		Set<String> keySet = serviceMap.keySet();
		for (String key : keySet) {
			List<ServiceModel> serviceModels = (List<ServiceModel>) (serviceMap.get(key));
			for (ServiceModel model : serviceModels) {
				serviceModelSet.add(model);
			}
		}

		RpcClientBootstrap clientBootstrap = null;
		for (ServiceModel model : serviceModelSet) {
			clientBootstrap = new RpcClientBootstrap(model.getHost(), model.getPort());
			clientBootstrap.start();
		}

		RpcClientContext.addServiceModel(serviceMap);

	}
}
