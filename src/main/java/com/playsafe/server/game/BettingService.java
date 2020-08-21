package com.playsafe.server.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.playsafe.server.game.event.EventListener;
import com.playsafe.server.game.event.EventType;
import com.playsafe.server.game.event.GameEvent;
import com.playsafe.server.game.event.Subject;
import com.playsafe.server.game.model.Bet;

public class BettingService implements EventListener, Subject {

	private final List<EventListener> observers = new ArrayList<>();
	private final List<Bet> bettingQueue;

	public BettingService() {
		bettingQueue = new LinkedList<>();
	}

	public boolean placeBet(Bet bet) {
		return bettingQueue.add(bet);
	}

	private void clearAllBets() {
		bettingQueue.clear();
	}

	@Override
	public void handleEvent(GameEvent event) {
		switch (event.getType()) {

		case SPIN_COMPLETE:
			int result = Integer.parseInt((String) event.getMessage());
			boolean resultIsEven = result % 2 == 0;
			StringBuilder resultMessage = new StringBuilder();
			resultMessage.append("\nNumber: " + result).append("\nPlayer\tBet\tOutcome\tWinnings").append("\n---");
			bettingQueue.forEach(bet -> {
				String playerName = bet.getPlayer().getUsername();
				float amountBetted = bet.getAmount();
				float winnings = 0.0f;

				if (bet.getSelection().equals("EVEN") && resultIsEven
						|| bet.getSelection().equals("ODD") && !resultIsEven) {
					winnings = amountBetted * 2;
					resultMessage.append(buildMessage(result, playerName, amountBetted, "WIN", winnings));
				} else if (Integer.parseInt(bet.getSelection()) == result) {
					winnings = amountBetted * 36;
					resultMessage.append(buildMessage(result, playerName, amountBetted, "WIN", winnings));
				} else {
					resultMessage.append(buildMessage(result, playerName, amountBetted, "LOSE", winnings));
				}
			});
			notifyObservers(resultMessage.toString());
			clearAllBets();
			break;

		case BET_ADDED:
			Bet bet = (Bet) event.getMessage();
			placeBet(bet);
			break;
		default:
			System.out.println("EventType not listed: " + event.getType());
			break;
		}

	}

	public String buildMessage(float result, String playerName, float amountBetted, String winLoss, float winnings) {
		return "\n" + playerName + "\t" + amountBetted + "\t" + winLoss + "\t" + winnings;
	}

	@Override
	public void notifyObservers(Object object) {
		GameEvent event = new GameEvent(EventType.BET_RESULTS, object);
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

}
