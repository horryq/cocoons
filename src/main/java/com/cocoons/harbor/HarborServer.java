package com.cocoons.harbor;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cocoons.actor.Actor;
import com.cocoons.actor.ActorMessage;
import com.cocoons.actor.MessageEntity;
import com.cocoons.actor.server.ActorNetClient;
import com.cocoons.actor.server.ActorNetServer;
import com.cocoons.common.Constants;

/**
 * @author qinguofeng
 */
public class HarborServer extends Actor {
	public static final String HARBOR = "harbor";

	private static final Logger logger = Logger.getLogger(HarborServer.class);

	private ActorNetServer server;

	private Map<String, ChannelHandlerContext> clientsMap = new HashMap<>();
	private Map<String, Long> connectingClients = new HashMap<>();

	public void startHarbor(Integer port) {
		server = new ActorNetServer();
		server.addHandler(new HarborHandler(this));
		server.start(1, 1, 0, port, 128);
	}

	public void dispatchHarborMessage(ActorMessage msg) {
		getSystem().sendMsgTo(msg.getReceiver(), msg);
	}

	public void sendRemote(ActorMessage msg) {
		String receiver = msg.getReceiver();
		ChannelHandlerContext client = clientsMap.get(receiver);
		if (client == null) {
			if (connectingClients.get(receiver) == null) {
				connectingClients.put(receiver, System.currentTimeMillis());
				ActorNetClient netClient = new ActorNetClient();
				netClient.addHandler(new HarborClientHandler(receiver, this));
				String url_port[] = Constants.URL_MAP.get(receiver).split(":");
				netClient.connect(url_port[0], Integer.parseInt(url_port[1]));
				logger.warn(System.currentTimeMillis() + ":" + "connect to:"
						+ receiver + ":" + msg + ":" + Thread.currentThread());
			}
			giveBackMessage();
		} else {
			client.writeAndFlush(msg);
		}
	}

	public void addConnectedClient(String key, ChannelHandlerContext client) {
		if (!clientsMap.containsKey(key)) {
			connectingClients.remove(key);
			clientsMap.put(key, client);
		} else {
			throw new IllegalStateException("repeated client connection for "
					+ key);
		}
	}

	public void removeConnectedClient(String key, ChannelHandlerContext client) {
		clientsMap.remove(key);
	}

	public void dispatchConnectedClient(String key,
			ChannelHandlerContext client, boolean remove) {
		getSystem().sendMsgTo(
				getSelfName(),
				new ActorMessage(ActorMessage.TYPE.TREQ, null, getSelfName(),
						getSelfName(), new MessageEntity(
								remove ? "removeConnectedClient"
										: "addConnectedClient", key, client)));
	}

	@Sharable
	static class HarborClientHandler extends ChannelInboundHandlerAdapter {
		private String tag;
		private HarborServer server;

		public HarborClientHandler(String tag, HarborServer server) {
			this.tag = tag;
			this.server = server;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			logger.warn("channelActive:" + ctx);
			server.dispatchConnectedClient(tag, ctx, false);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			logger.warn("channelInactive:" + ctx);
			server.dispatchConnectedClient(tag, ctx, false);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			super.exceptionCaught(ctx, cause);
			// TODO ...
		}

	}

	@Sharable
	static class HarborHandler extends ChannelInboundHandlerAdapter {
		private HarborServer server;

		public HarborHandler(HarborServer server) {
			this.server = server;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			if (msg instanceof ActorMessage) {
				server.dispatchHarborMessage((ActorMessage) msg);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			// TODO ...
			super.exceptionCaught(ctx, cause);
		}
	}
}
