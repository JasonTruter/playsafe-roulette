package com.playsafe.server.game.model;

import io.netty.channel.Channel;

public class Session {

	private final Channel channel;

	public Session(Channel channel) {
		this.channel = channel;
	}

	public void pushMessage(String message) {
		this.channel.writeAndFlush(message + "\r\n");
	}

}
