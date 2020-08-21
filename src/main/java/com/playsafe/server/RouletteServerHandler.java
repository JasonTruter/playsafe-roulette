package com.playsafe.server;

import com.playsafe.server.game.PlayerService;
import com.playsafe.server.game.model.Player;
import com.playsafe.server.game.model.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RouletteServerHandler extends SimpleChannelInboundHandler<String> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private final PlayerService playerHandler;

	public RouletteServerHandler(PlayerService playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		channels.add(incoming);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		channels.remove(incoming);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		Channel incoming = ctx.channel();
		String username = message.split(" ")[0];
		Player player = playerHandler.getPlayer(username);

		if (player == null) {
			incoming.writeAndFlush(username + " is not registered! \n");
			return;
		}

		if (player.getSession() == null) {
			player.setSession(new Session(incoming));
			playerHandler.updatePlayer(player);
		}
		// TODO: make ChannelGroup the source of truth, there is a problem where since
		// we load users on start of the application
		// Right now there is no user login system built, so a user has to send a
		// message on their client before they are considered online.
		playerHandler.handleMessage(message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(cause);
	}
}
