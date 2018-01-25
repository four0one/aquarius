package com.rpcframework.core;

import com.rpcframework.core.codec.MessageDecoder;
import com.rpcframework.core.codec.MessageEncoder;
import com.rpcframework.core.handler.RpcClientHandler;
import com.rpcframework.core.heartbeat.HeartBeatClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wei.chen1
 * @since 2018/1/17
 */
public class RpcClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {

	@Override
	protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
		ChannelPipeline pipeline = nioSocketChannel.pipeline();
		pipeline.addLast("decode", new MessageDecoder());
		pipeline.addLast("encode", new MessageEncoder());
//		pipeline.addLast("decode",new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
//		pipeline.addLast("encode",new ObjectEncoder());
		pipeline.addLast("idle", new IdleStateHandler(0,50,0, TimeUnit.SECONDS));
		pipeline.addLast("sendMsgHandler",new RpcClientHandler());
		pipeline.addLast("pingHandler",new HeartBeatClientHandler());
	}
}
