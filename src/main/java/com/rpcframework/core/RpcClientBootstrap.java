package com.rpcframework.core;

import com.rpcframework.core.codec.MessageDecoder;
import com.rpcframework.core.codec.MessageEncoder;
import com.rpcframework.core.handler.RpcClientHandler;
import com.rpcframework.core.heartbeat.HeartBeatClientHandler;
import com.rpcframework.core.heartbeat.ReconnectProcessor;
import com.rpcframework.core.pool.PooledChannelHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcClientBootstrap {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String host;

	private int port;

	private Bootstrap bootstrap;

	private RpcClientChannelInitializer handler;

	private EventLoopGroup group;

	private RpcClientBootstrapContext context = RpcClientBootstrapContext.getInstance();

	public RpcClientBootstrap() {

	}

	public RpcClientBootstrap(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() {
		group = new NioEventLoopGroup();
		try {
			bootstrap = new Bootstrap();
			handler = new RpcClientChannelInitializer();
			bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
					.handler(handler);
			ChannelFuture channelFuture = bootstrap.connect().sync();
			channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
				if (channelFuture1.isSuccess()) {
					logger.debug("{}:{}RPC客户端启动成功", host, port);
					context.addBootstrap(host, port, this);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			group.shutdownGracefully();
		}
	}

	public Channel connect(PooledChannelHolder pooledChannelHolder) {
		group = new NioEventLoopGroup();
		try {
			bootstrap = new Bootstrap();
			handler = new RpcClientChannelInitializer(pooledChannelHolder);
			bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(pooledChannelHolder.getHost(), pooledChannelHolder.getPort())
					.handler(handler);
			ChannelFuture channelFuture = bootstrap.connect().sync();
			channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
				if (channelFuture1.isSuccess()) {
					logger.debug("{}:{}RPC客户端启动成功", pooledChannelHolder.getHost(), pooledChannelHolder.getPort());
				}
			});
			return channelFuture.channel();
		} catch (Exception e) {
			e.printStackTrace();
			group.shutdownGracefully();
		}
		return null;
	}

	public void restart(ReconnectProcessor processor) {
		try {
			ChannelFuture future = bootstrap.connect();
			future.addListener((ChannelFutureListener) channelFuture -> {
				if (channelFuture.isSuccess()) {
					processor.cancel(true);
				}
			});
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}


}
