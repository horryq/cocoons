package com.cocoons.actor.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.List;

import com.cocoons.net.NetClient;

/**
 * @author qinguofeng
 */
public class ActorNetClient {
	private NetClient client;

	private List<ChannelHandler> handlerList = new ArrayList<>();

	public void addHandler(ChannelHandler handler) {
		handlerList.add(handler);
	}

	public ChannelFuture connect(String host, int port) {
		if (handlerList.size() <= 0)
			throw new IllegalStateException("Must addHandler first.");
		client = new NetClient(new ActorClientHandlerInitializer(
				handlerList.toArray(new ChannelHandler[0])));
		return client.connect(host, port);
	}

	static class ActorClientHandlerInitializer extends
			ChannelInitializer<SocketChannel> {
		private ChannelHandler[] handlers;

		public ActorClientHandlerInitializer(ChannelHandler[] handlers) {
			this.handlers = handlers;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new HarborMsgDecoder());
			pipeline.addLast(new HarborMsgEncoder());
			pipeline.addLast(handlers);
		}

	}
}
