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
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
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
public class ClientRpcServiceProxyProcessor implements BeanPostProcessor, InitializingBean, PathChildrenCacheListener {

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
			ConsistencyHashService hashService = new ConsistencyHashService();
			ConsistencyHashRing hashRing = hashService.generateHashRing(serviceModels);
			ServiceRegistMapContext.addRpcServiceHashRing(key, hashRing);
			serviceDiscoverer.listenService(key, this);
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

	@Override
	public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
		String path = event.getData().getPath();
		if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {  // 添加子节点时触发
			String serviceName = path.substring(0, path.lastIndexOf("/"));
			ServiceRegistMapContext.addRpcServiceHashRing(serviceName, serviceDiscoverer.generateServiceRing(serviceName));
			String address = new String(event.getData().getData());
			String[] hostAndPort = address.split(":");
			PooledChannelHolder holder = new PooledChannelHolder(hostAndPort[0],Integer.parseInt(hostAndPort[1]));
			holder.initChannel();
			RpcClientBootstrapContext.getInstance().addPooledChannelHolder(holder);
		}

		if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
			String serviceName = path.substring(0, path.lastIndexOf("/"));
			ServiceRegistMapContext.addRpcServiceHashRing(serviceName, serviceDiscoverer.generateServiceRing(serviceName));
			String address = new String(event.getData().getData());
			String[] hostAndPort = address.split(":");
			RpcClientBootstrapContext.getInstance().removePooledChannelHolder(hostAndPort[0],Integer.parseInt(hostAndPort[1]));
		}
	}


	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
