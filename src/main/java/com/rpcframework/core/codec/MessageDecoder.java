package com.rpcframework.core.codec;

import com.rpcframework.core.codec.kryo.KryoSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class MessageDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private KryoSerialize kryoSerialize = new KryoSerialize();

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() < 4) {
			return;
		}
		byteBuf.markReaderIndex();
		int dataLength = byteBuf.readInt();
		if (dataLength <= 0) {
			channelHandlerContext.close();
		}
		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return;
		} else {
			byte[] messageBody = new byte[dataLength];
			byteBuf.readBytes(messageBody);

			try {
				//反序列化
				Object obj = kryoSerialize.deserialize(messageBody);
				list.add(obj);
			} catch (IOException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
}
