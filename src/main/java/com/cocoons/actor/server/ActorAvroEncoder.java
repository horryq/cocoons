package com.cocoons.actor.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import com.cocoons.actor.server.protocol.ActorRemoteMsg;

/**
 *
 * @author qinguofeng
 */
public class ActorAvroEncoder extends MessageToByteEncoder<ActorRemoteMsg> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ActorRemoteMsg msg,
			ByteBuf out) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().binaryEncoder(bos, null);
		SpecificDatumWriter<ActorRemoteMsg> writer = new SpecificDatumWriter<ActorRemoteMsg>(
				ActorRemoteMsg.class);
		writer.write(msg, encoder);
		encoder.flush();
		byte[] bs = bos.toByteArray();
		// ByteBuffer bb = ByteBuffer.allocate(bs.length + 4);
		// bb.putInt(bs.length);
		// bb.put(bs);
		out.writeInt(bs.length);
		out.writeBytes(bs);
		bos.close();
	}
}
