package com.playsafe.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.channel.Channel;

public class Game {
	private Channel channel;
	private BufferedReader in;
	private static final String FORMAT = "[username] [number/ODD/EVEN] [bet_amount] \n";

	public Game(Channel channel) {
		this.channel = channel;
		this.in = new BufferedReader(new InputStreamReader(System.in));
	}

	public void start() throws IOException {
		System.out.println("Welcome to PlaySafe Roulette!");
		System.out.println("Place your bet: " + FORMAT + "(eg. Barbara 13 100)");
		while (true) {
			placeBet();
		}
	}

	private void placeBet() throws IOException {
		String request = in.readLine() + "\r\n";
		if (valid(request)) {
			channel.writeAndFlush(request);
		} else {
			System.out.println("Invalid, use the following format: " + FORMAT);
		}

	}

	private boolean valid(String request) {
		try {
			String[] values = request.split(" ");

			if (values.length > 3) {
				return false;
			}
			String selection = values[1];
			String betAmount = values[2];
			try {
				Float.parseFloat(betAmount);
			} catch (NumberFormatException ex) {
				return false;
			}

			if (!selection.equalsIgnoreCase("EVEN") && !selection.equalsIgnoreCase("ODD")) {
				try {
					int number = Integer.parseInt(selection);
					if (number < 1 && number > 36) {
						return false;
					}
				} catch (NumberFormatException ex) {
					return false;
				}

			} else {
				return true;
			}

		} catch (ArrayIndexOutOfBoundsException ex) {
			return false;
		}

		return true;
	}

}
