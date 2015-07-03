package com.cocoons.actor.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.cocoons.actor.ActorMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * @author qinguofeng
 */
public class HarborMsgDecoder extends ByteToMessageDecoder {

	private Kryo kryo = new Kryo();

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		// int is 32bit, 4 bytes
		if (msg.isReadable() && msg.readableBytes() > 4) {
			msg.markReaderIndex();
			int msgSize = msg.readInt();
			int totalSize = msg.readableBytes();
			if (msgSize > totalSize) {
				msg.resetReaderIndex();
			}

			ByteBuf msgBuf = msg.readBytes(msgSize);
			Input input = new Input(msgBuf.array());
			ActorMessage msgObj = kryo.readObject(input, ActorMessage.class);
			out.add(msgObj);

			msg.discardReadBytes();
		}

	}

}
