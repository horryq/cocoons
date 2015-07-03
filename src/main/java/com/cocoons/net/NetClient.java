package com.cocoons.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author qinguofeng
 */
public class NetClient {

	private ChannelHandler handler;

	public NetClient(ChannelHandler handler) {
		this.handler = handler;
	}

	public ChannelFuture connect(String host, int port) {
		Bootstrap boot = new Bootstrap();
		EventLoopGroup loopGroup = new NioEventLoopGroup(1);
		boot.group(loopGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(handler);
		return boot.connect(host, port);
	}
}
