package com.playsafe.server.game.event;

public class GameEvent {

	private final EventType type;
	private final Object message;

	public GameEvent(EventType type, Object message) {
		this.type = type;
		this.message = message;
	}

	public Object getMessage() {
		return this.message;
	}
	
	public EventType getType() {
		return this.type;
	}
}
