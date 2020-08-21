package com.playsafe.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.playsafe.server.game.BettingService;
import com.playsafe.server.game.PlayerService;
import com.playsafe.server.game.SpinningService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class RouletteServer {

	private final int port;

	private final SpinningService spinningService = new SpinningService();
	private final BettingService bettingService = new BettingService();
	private final PlayerService playerService = new PlayerService();

	public RouletteServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		new RouletteServer(8000).start();
	}

	public void start() throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipe = ch.pipeline();
							pipe.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
							pipe.addLast("decoder", new StringDecoder());
							pipe.addLast("encoder", new StringEncoder());

							pipe.addLast(new RouletteServerHandler(playerService));
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = bootstrap.bind(port).sync();

			if (f.isDone()) {
				loadPlayerData();
				System.out.println("Registering event listeners");
				spinningService.addObserver(bettingService);
				playerService.addObserver(bettingService);
				bettingService.addObserver(playerService);
				spinningService.start();
				System.out.println("Server listening on port: " + port);
			}
			f.channel().closeFuture().sync();

		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	private void loadPlayerData() {
		System.out.println("Loading players...");
		try (Stream<String> stream = Files.lines(Paths.get(Constants.PLAYER_DATA_FILE))) {
			stream.forEach(player -> {
				playerService.register(player);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
