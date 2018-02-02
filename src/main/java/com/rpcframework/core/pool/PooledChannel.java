package com.rpcframework.core.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Channel代理类
 *
 * @author wei.chen1
 * @since 2018/2/2
 */
public class PooledChannel implements InvocationHandler {

	private Channel channel;

	private PooledChannelHolder holder;

	public PooledChannel(Channel channel, PooledChannelHolder holder) {
		this.channel = channel;
		this.holder = holder;
	}

	public ChannelFuture writeAndFlush(Object o) {
		return channel.writeAndFlush(o);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = method.invoke(proxy, args);
		if (method.getName().equals("writeAndFlush")) {
			//归还channel
			this.holder.pushChannel(this);
		}
		return result;
	}
}
