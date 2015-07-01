package com.cocoons.actor.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import com.cocoons.actor.server.protocol.ActorRemoteMsg;

/**
 *
 * @author qinguofeng
 */
public class ActorAvroDecoder extends ByteToMessageDecoder {

	private BinaryDecoder mBinaryDecoder;
	private DatumReader<ActorRemoteMsg> mDatumReader;

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
			mBinaryDecoder = DecoderFactory.get().binaryDecoder(msgBuf.array(),
					mBinaryDecoder);
			// TODO ... difference between SpecificDatumReader and
			// ReflectDatumReader
			mDatumReader = new SpecificDatumReader<ActorRemoteMsg>(ActorRemoteMsg.class);
			// TODO ... do not reuse message.is it right?
			ActorRemoteMsg msgObj = mDatumReader.read(null, mBinaryDecoder);
			out.add(msgObj);

			msg.discardReadBytes();
		}

	}

}
