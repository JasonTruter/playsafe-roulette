package com.playsafe.server.game.model;

import java.util.LinkedList;
import java.util.List;

public class Player {

	private String username;
	private Session session;
	private List<Bet> bets = new LinkedList<>();

	public Player(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public boolean addBet(Bet bet) {
		return bets.add(bet);
	}
}
