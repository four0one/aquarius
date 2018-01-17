package com.rpcframework.core;

import com.rpcframework.core.codec.MessageDecoder;
import com.rpcframework.core.codec.MessageEncoder;
import com.rpcframework.core.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wei.chen1
 * @since 2018/1/16
 */
public class RpcClientBootstrap {

	public void start(String host, int port) {
		EventLoopGroup group = new NioEventLoopGroup(2);
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline pipeline = socketChannel.pipeline();
							pipeline.addLast("decode", new MessageDecoder());
							pipeline.addLast("encode", new MessageEncoder());
							pipeline.addLast(new RpcClientHandler());
						}
					});
			ChannelFuture channelFuture = bootstrap.connect().sync();
			channelFuture.channel().closeFuture();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
