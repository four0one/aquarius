package com.rpcframework.core.executor;

import com.rpcframework.monitor.ServiceModel;
import io.netty.channel.Channel;

/**
 * @author wei.chen1
 * @since 2018/1/26
 */
public class ClientExecutorContext {

	private Channel channel;

	//当前正在使用的服务地址
	private ServiceModel serviceModel;

	public ServiceModel getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
