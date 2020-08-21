package com.playsafe.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RouletteClientHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println(msg);
	}

}
