package com.rpcframework.core.codec;

import com.rpcframework.core.codec.kryo.KryoSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wei.chen1
 * @since 2018/1/15
 */
public class MessageEncoder extends MessageToByteEncoder {

	private KryoSerialize kryoSerialize = new KryoSerialize();

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		byte[] objectBytes = kryoSerialize.serialize(o);
		if (objectBytes == null || objectBytes.length <= 0) {
			byteBuf.writeBytes(Unpooled.copyInt(0));
			return;
		}
		byteBuf.writeInt(objectBytes.length);
		byteBuf.writeBytes(objectBytes);
	}
}
