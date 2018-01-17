package com.rpcframework.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端接受请求处理器
 * @author wei.chen1
 * @since 2018/1/15
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info(" 收到请求 : {}", msg);

		//TODO 处理服务调用，线程or调用
	}

}
