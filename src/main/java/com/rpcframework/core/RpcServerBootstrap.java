package com.rpcframework.core;

import com.rpcframework.core.codec.MessageDecoder;
import com.rpcframework.core.codec.MessageEncoder;
import com.rpcframework.core.handler.RpcServerHandler;
import com.rpcframework.core.heartbeat.HeartBeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcServerBootstrap {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private EventLoopGroup boss = new NioEventLoopGroup();
	//自定义工作线程组，线程数为核心数的2倍，使用自定义前缀的线程工厂
	private EventLoopGroup worker = new NioEventLoopGroup(8);

	public void start(int port) {
		port = port == 0 ? 8088 : port;
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("decode",new MessageDecoder());
						pipeline.addLast("encode",new MessageEncoder());
						pipeline.addLast("idle", new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
						pipeline.addLast("rpcServerHandler", new RpcServerHandler());
						pipeline.addLast("pongHandler", new HeartBeatServerHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture channelFuture = bootstrap.bind().sync();
			int finalPort = port;
			channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
				if (channelFuture1.isSuccess()) {
					logger.debug("RPC服务端启动成功,端口为：{}", finalPort);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	public void stop() {
		boss.shutdownGracefully();
		worker.shutdownGracefully();
	}

}
