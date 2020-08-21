package com.playsafe.server.game.model;

import java.util.Date;

public class Bet {

	private final String selection;
	private final float amount;
	private final Date created;
	private final Player player;

	public Bet(String selection, float amount, Player player) {
		this.selection = selection;
		this.amount = amount;
		this.player = player;
		this.created = new Date();
	}

	public String getSelection() {
		return selection;
	}

	public float getAmount() {
		return amount;
	}

	public Date getCreated() {
		return created;
	}

	public Player getPlayer() {
		return player;
	}

}
