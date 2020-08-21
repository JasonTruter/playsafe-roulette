package com.playsafe.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.playsafe.server.game.event.EventListener;
import com.playsafe.server.game.event.EventType;
import com.playsafe.server.game.event.GameEvent;
import com.playsafe.server.game.event.Subject;
import com.playsafe.server.game.model.Bet;
import com.playsafe.server.game.model.Player;

public class PlayerService implements Subject, EventListener {

	private List<EventListener> observers = new ArrayList<>();
	private Map<String, Player> players = new ConcurrentHashMap<>();

	public Player getPlayer(String username) {
		return players.get(username);
	}

	public List<Player> getPlayers() {
		return players.values().stream().collect(Collectors.toList());
	}

	public boolean updatePlayer(Player player) {
		if (players.containsKey(player.getUsername())) {
			players.put(player.getUsername(), player);
			return true;
		} else {
			return false;
		}
	}

	public void handleMessage(String message) {
		String[] values = message.split(" ");

		String username = values[0];
		String selection = values[1];
		String betAmount = values[2];

		Player player = getPlayer(username);
		Bet bet = new Bet(selection, Float.parseFloat(betAmount), player);
		player.addBet(bet);
		notifyObservers(bet);
		broadcastMessage(username + " placed a bet with a selection of " + bet.getSelection() + " and an amount of "
				+ bet.getAmount());
	}

	public void broadcastMessage(String message) {
		getPlayers().forEach(player -> {
			if (player.getSession() != null) {
				player.getSession().pushMessage("\n[SERVER]: " + message);
			}
		});
	}

	public void register(String player) {
		players.put(player, new Player(player));
	}

	@Override
	public void notifyObservers(Object bet) {
		GameEvent event = new GameEvent(EventType.BET_ADDED, bet);
		observers.forEach(observer -> observer.handleEvent(event));

	}

	@Override
	public void addObserver(EventListener observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(EventListener observer) {
		observers.remove(observer);

	}

	@Override
	public void handleEvent(GameEvent event) {
		switch (event.getType()) {
		
		case BET_RESULTS:
			String betResults = event.getMessage().toString();
			broadcastMessage(betResults);
			break;
		default:
			System.out.println("EventType not listed: " + event.getType());
			break;
		}

	}
}
