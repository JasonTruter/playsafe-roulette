package com.playsafe.server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.playsafe.server.Constants;
import com.playsafe.server.game.event.EventListener;
import com.playsafe.server.game.event.EventType;
import com.playsafe.server.game.event.GameEvent;
import com.playsafe.server.game.event.Subject;

public class SpinningService implements Runnable, Subject {

	private List<EventListener> observers = new ArrayList<>();

	private void spin() {
		int spinResult = getRandomNumber(Constants.ROULETTE_MIN_NUMBER, Constants.ROULETTE_MAX_NUMBER);
		this.notifyObservers(spinResult);
	}

	public void start() {
		System.out.println("Starting spinning service scheduler.");
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this, 0, Constants.SPIN_DELAY_IN_SECONDS, TimeUnit.SECONDS);
	}

	public int getRandomNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public void run() {
		spin();
	}

	@Override
	public void notifyObservers(Object o) {
		observers.forEach(observer -> {
			GameEvent event = new GameEvent(EventType.SPIN_COMPLETE, Integer.toString((int) o));
			observer.handleEvent(event);
		});

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
