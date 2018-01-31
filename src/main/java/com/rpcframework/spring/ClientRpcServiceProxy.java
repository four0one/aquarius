package com.rpcframework.spring;

import com.rpcframework.core.ClientTransceiver;
import com.rpcframework.core.RpcRequest;

import com.rpcframework.core.RpcResponse;
import com.rpcframework.exception.RpcRequestException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 接口服务代理，远程调用接口返回结果
 *
 * @author wei.chen1
 * @since 2018/1/16
 */
public class ClientRpcServiceProxy implements MethodInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ClientTransceiver clientTransceiver = ClientTransceiver.getInstance();

	private ExecutorService requestCallThreadPool = Executors.newFixedThreadPool(16);

	private static class ClientRpcServiceProxyHolder {
		private static ClientRpcServiceProxy INSTANCE = new ClientRpcServiceProxy();
	}

	public static ClientRpcServiceProxy getInstance() {
		return ClientRpcServiceProxyHolder.INSTANCE;
	}

	public void injectProxy(Field field, Object bean) throws RpcRequestException {
		if (!field.getType().isInterface()) {
			throw new RpcRequestException("rpc实例注入失败，该实例声明类型不是接口");
		}
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(new Class[]{field.getType()});
		enhancer.setCallback(this);
		enhancer.setClassLoader(bean.getClass().getClassLoader());
		Object implBean = enhancer.create();
		field.setAccessible(true);
		ReflectionUtils.setField(field, bean, implBean);
		field.setAccessible(false);
	}

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		RpcRequest request = new RpcRequest();
		request.setMethodName(method.getName());
		request.setParameters(objects);
		Class<?>[] parameterTypes = method.getParameterTypes();
		request.setParameterTypes(parameterTypes);
		Class<?>[] interfaces = o.getClass().getInterfaces();
		if (interfaces == null || interfaces.length == 1) {
			throw new RpcRequestException("rpc请求失败，该服务不是接口");
		}
		for (Class inf : interfaces) {
			if (!inf.isAssignableFrom(Factory.class)) {
				request.setService(inf.getName());
				break;
			}
		}
		//发送请求
		Future<RpcResponse> responseFuture = requestCallThreadPool.submit(new Callable<RpcResponse>() {
			@Override
			public RpcResponse call() throws Exception {
				return clientTransceiver.sendRequest(request);
			}
		});
		RpcResponse response = responseFuture.get();
		logger.debug("requestId:{},return data {}",response.getRequestId(),response.getResult());
		return response.getResult();
	}
}
