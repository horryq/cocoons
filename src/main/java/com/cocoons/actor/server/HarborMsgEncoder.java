package com.cocoons.actor.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

import com.cocoons.actor.ActorMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author qinguofeng
 */
public class HarborMsgEncoder extends MessageToByteEncoder<ActorMessage> {

	private Kryo kryo = new Kryo();

	@Override
	protected void encode(ChannelHandlerContext ctx, ActorMessage msg,
			ByteBuf out) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Output output = new Output(bos);
		kryo.writeObject(output, msg);
		output.flush();
		byte[] bs = bos.toByteArray();
		out.writeInt(bs.length);
		out.writeBytes(bs);
		bos.close();
	}
}
