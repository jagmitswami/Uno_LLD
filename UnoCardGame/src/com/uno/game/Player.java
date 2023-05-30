package com.uno.game;

import java.util.LinkedList;

public class Player {

	private int id;
	private String name;

	private LinkedList<Card> playerHand;

	public Player(int id, String name) {
		this.id = id;
		this.name = name;
		this.playerHand = new LinkedList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<Card> getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(LinkedList<Card> playerHand) {
		this.playerHand = playerHand;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + "]";
	}

}
