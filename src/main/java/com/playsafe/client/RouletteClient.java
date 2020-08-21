package com.playsafe.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class RouletteClient {

	private final String host;
	private final int port;

	public RouletteClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		new RouletteClient("localhost", 8000).start();
	}

	private void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipe = ch.pipeline();
					pipe.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
					pipe.addLast("decoder", new StringDecoder());
					pipe.addLast("encoder", new StringEncoder());

					pipe.addLast("handler", new RouletteClientHandler());
				}
			});

			Channel channel = bootstrap.connect(host, port).sync().channel();
			new Game(channel).start();
		} finally {
			group.shutdownGracefully();
		}
	}

}
