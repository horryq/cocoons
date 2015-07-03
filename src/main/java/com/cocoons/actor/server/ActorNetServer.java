package com.cocoons.actor.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.ArrayList;
import java.util.List;

import com.cocoons.net.NetServer;

/**
 * @author qinguofeng
 */
public class ActorNetServer {
	private NetServer server;

	private List<ChannelHandler> handlerList = new ArrayList<>();

	public void addHandler(ChannelHandler handler) {
		handlerList.add(handler);
	}

	public ChannelFuture start(int bossNum, int workerNum, int logicNum,
			int port, int backlog) {
		if (handlerList.size() <= 0)
			throw new IllegalStateException("Must addHandler first.");

		EventExecutorGroup executorGroup = null;
		if (logicNum > 0) {
			executorGroup = new DefaultEventExecutorGroup(logicNum);
		}
		server = new NetServer(new ActorChannelHandlerInitializer(
				executorGroup, handlerList.toArray(new ChannelHandler[0])));
		return server.start(bossNum, workerNum, port, backlog);
	}

	static class ActorChannelHandlerInitializer extends
			ChannelInitializer<SocketChannel> {
		private EventExecutorGroup executorGroup;
		private ChannelHandler[] channelHandlers;

		public ActorChannelHandlerInitializer(EventExecutorGroup executorGroup,
				ChannelHandler[] handlers) {
			this.executorGroup = executorGroup;
			this.channelHandlers = handlers;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new HarborMsgDecoder());
			pipeline.addLast(new HarborMsgEncoder());
			if (executorGroup != null) {
				pipeline.addLast(executorGroup, channelHandlers);
			} else {
				pipeline.addLast(channelHandlers);
			}
		}

	}
}
