package com.rpcframework.core.pool;

import com.rpcframework.core.RpcClientBootstrap;
import com.rpcframework.core.heartbeat.ReconnectProcessor;
import io.netty.channel.Channel;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/2/2
 */
public class PooledChannelHolder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	//最大活动连接数
	protected int poolMaximumActiveConnections = 32;

	//最大空闲连接数
	protected int poolMaximumIdleConnections = 5;


	private final PooledState state = new PooledState();

	private String host;

	private int port;

	public PooledChannelHolder(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Channel popChannel() {
		Channel pooledChannel = null;
//        state.lock();
//        try {
//            if (!state.isIdelChannelListEmpty()) {
//                pooledChannel = state.getIdelChannel();
//            } else {
//                if (state.activeChannelSize() < poolMaximumActiveConnections) {
//                    //创建新的连接
//                    logger.debug("开启新的连接");
//                    RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
//                    pooledChannel = clientBootstrap.connect(this);
//                    state.addActiveChannel(pooledChannel);
//                } else {
//                    //等待
//                    do {
//                        state.waitIdle();
//                    } while (state.idleChannelSize() == 0);
//                    pooledChannel = state.getIdelChannel();
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            state.unlock();
//        }
//		long counts = state.getReqCounts().incrementAndGet();
		pooledChannel = state.getIdelChannel(RandomUtils.nextInt(0,poolMaximumIdleConnections));

		return pooledChannel;
	}

	public void initChannel() {
		logger.debug("初始化连接");
		for (int i = 0; i < poolMaximumIdleConnections; i++) {
			RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
			Channel channel = clientBootstrap.connect(this);
			state.addIdelChannel(channel);
		}
	}

	public void reconnect(ReconnectProcessor processor) {
		logger.debug("重新连接");
		state.removeAllChannel();
		for (int i = 0; i < poolMaximumIdleConnections; i++) {
			RpcClientBootstrap clientBootstrap = new RpcClientBootstrap();
			Channel channel = clientBootstrap.connect(this);
			if (channel != null) {
				state.addIdelChannel(channel);
				processor.cancel(true);
			}
		}
	}


	public void pushChannel(Channel pooledChannel) {
		/*state.lock();
		try {
			state.removeActiveChannel(pooledChannel);
			if (state.idleChannelSize() < poolMaximumIdleConnections) {
				state.addIdelChannel(pooledChannel);
				logger.debug("存放到空闲连接池");
				state.signIdle();
			} else {
				logger.debug("关闭多余连接");
				pooledChannel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			state.unlock();
			logger.debug("i:{},a:{}", state.idleChannelSize(), state.activeChannelSize());
		}*/

//		state.getReqCounts().decrementAndGet();
	}

	public String getHost() {
		return host;
	}


	public int getPort() {
		return port;
	}


}
