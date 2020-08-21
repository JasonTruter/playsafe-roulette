package com.playsafe.server.game.event;

public interface Subject {

	public void notifyObservers(Object object);

	public void addObserver(EventListener observer);

	public void removeObserver(EventListener observer);

}
