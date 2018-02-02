package com.rpcframework.core.pool;

import com.rpcframework.core.RpcClientBootstrap;
import com.rpcframework.core.heartbeat.ReconnectProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/2/2
 */
public class PooledChannelHolder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//最大活动连接数
	protected int poolMaximumActiveConnections = 10;

	//最大空闲连接数
	protected int poolMaximumIdleConnections = 5;

	private PooledChannel currentPooledChannel;

	private final PooledState state = new PooledState();

	private String host;

	private int port;

	public PooledChannelHolder(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void channelActiveCallback(Channel channel) {
		this.currentPooledChannel = new PooledChannel(channel, this);
		state.lock();
		state.signal();
		state.unlock();
	}

	public PooledChannel popChannel() {
		PooledChannel pooledChannel = null;
		state.lock();
		try {
			if (!state.isIdelChannelListEmpty()) {
				pooledChannel = state.getIdelChannel();
			} else {
				if (state.activeChannelSize() < poolMaximumActiveConnections) {
					//创建新的连接
					logger.debug("开启新的连接");
					RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
					clientBootstrap.start(this);
					state.await();
					pooledChannel = this.currentPooledChannel;
				} else {
					//等待
					state.waitIdle();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			state.unlock();
		}
		return pooledChannel;
	}

	public void initChannel() {
		state.lock();
		try {
			logger.debug("初始化连接");
			for (int i = 0; i < poolMaximumIdleConnections; i++) {
				RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
				clientBootstrap.start(this);
				state.await();
				state.addIdelChannel(this.currentPooledChannel);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			state.unlock();
		}
	}

	public void reconnectChannel(ReconnectProcessor processor) {
		state.lock();
		try {
			logger.debug("重新连接");
			state.removeAllChannel();
			for (int i = 0; i < poolMaximumIdleConnections; i++) {
				RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
				clientBootstrap.start(this);
				state.await();
				state.addIdelChannel(this.currentPooledChannel);
				processor.cancel(true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			state.unlock();
		}
	}


	public void pushChannel(PooledChannel pooledChannel) {
		state.lock();
		try {
			state.removeActiveChannel(pooledChannel);
			if (state.idleChannelSize() < poolMaximumIdleConnections) {
				state.addIdelChannel(pooledChannel);
			}
			state.signIdle();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			state.unlock();
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
