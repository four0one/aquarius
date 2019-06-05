package com.rpcframework.spring;

import com.rpcframework.annotation.RpcResource;
import com.rpcframework.core.RpcClientBootstrapContext;
import com.rpcframework.core.executor.ConsistencyHashRing;
import com.rpcframework.core.executor.ConsistencyHashService;
import com.rpcframework.core.handler.ServiceRegistMapContext;
import com.rpcframework.core.pool.PooledChannelHolder;
import com.rpcframework.discoverer.ServiceDiscoverer;
import com.rpcframework.discoverer.ServiceDiscovererImpl;
import com.rpcframework.exception.RpcRequestException;
import com.rpcframework.monitor.ServiceModel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class ClientRpcServiceProxyProcessor implements BeanPostProcessor,InitializingBean {

	private String config;

	private ServiceDiscoverer serviceDiscoverer;


	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		ClientRpcServiceProxy proxy = ClientRpcServiceProxy.getInstance();
		Class<?> cls = bean.getClass();
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(RpcResource.class)) {
				try {
					proxy.injectProxy(field, bean);
				} catch (RpcRequestException e) {
					throw e;
				}
			}
		}
		return bean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		serviceDiscoverer = new ServiceDiscovererImpl(config);
		Map<String, List<ServiceModel>> serviceMap = serviceDiscoverer.find();
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
			ServiceRegistMapContext.addServiceModel(key, copyList);
			ConsistencyHashService hashService = new ConsistencyHashService();
			ConsistencyHashRing hashRing = hashService.generateHashRing(serviceModels);
			ServiceRegistMapContext.addRpcServiceHashRing(key, hashRing);
		}

		//打开channel
		openServiceChannel(serviceModelSet);

	}

	private void openServiceChannel(Set<ServiceModel> serviceModelSet) {
		PooledChannelHolder holder = null;
		for (ServiceModel model : serviceModelSet) {
			holder = new PooledChannelHolder(model.getHost(), model.getPort());
			holder.initChannel();
			RpcClientBootstrapContext.getInstance().addPooledChannelHolder(holder);
		}
	}


	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
