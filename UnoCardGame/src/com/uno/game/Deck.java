package com.uno.game;

import java.util.Random;
import java.util.Stack;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;

public class Deck {

	private Stack<Card> cards;

	public Deck() {
		this.cards = new Stack<>();
	}

	/*
	 * Total cards 108, to initialize deck and getting all cards for the game inside
	 * the deck
	 */
	public void initializeDeck() {

		Color[] colors = Color.values();
		for (Color color : colors) {
			if (color == Color.WILD)
				continue;

			/* One card per color */
			cards.push(new Card(color, Value.ZERO));

			Value[] values = Value.values();
			for (Value value : values) {
				if (value == Value.ZERO || value == Value.NONE || value == Value.PLUS_4)
					continue;

				/* Two cards per color of each value (excluding above contioned) */
				cards.push(new Card(color, value));
				cards.push(new Card(color, value));
			}
		}

		/* 4+4 special (wild) cards per deck */
		for (int i = 0; i < 4; i++) {
			cards.push(new Card(Color.WILD, Value.NONE));
			cards.push(new Card(Color.WILD, Value.PLUS_4));
		}

	}

	/* Shuffling the cards */
	public void shuffleCards() {
		int suffleTimes = new Random().nextInt(500) + 500;

		for (int j = 0; j < suffleTimes; j++) {
			int x = cards.size() - 1;
			int suffle = new Random().nextInt(x / 3);
			while (suffle < x) {
				Card temp = cards.get(x);
				cards.set(x, cards.get(suffle));
				cards.set(suffle, temp);
				x--;
				suffle++;
			}
		}
	}

	/* getter-setters */
	public Stack<Card> getCards() {
		return cards;
	}

	public void setCards(Stack<Card> cards) {
		this.cards = cards;
	}

}
