package com.cocoons.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author qinguofeng
 */
public class NetServer {

	private ChannelHandler channelHandler;

	public NetServer(ChannelHandler handler) {
		channelHandler = handler;
	}

	public ChannelFuture start(int bossNum, int workerNum, int port, int backlog) {
		EventLoopGroup boosLoopGroup = new NioEventLoopGroup(bossNum);
		EventLoopGroup workerLoopGroup = new NioEventLoopGroup(workerNum);

		ServerBootstrap server = new ServerBootstrap();

		server.group(boosLoopGroup, workerLoopGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, backlog)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childHandler(channelHandler);

		return null;
	}

}
