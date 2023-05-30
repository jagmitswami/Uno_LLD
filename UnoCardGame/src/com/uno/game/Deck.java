package com.uno.game;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;

public class Deck {

	private Deque<Card> cards;
	private int cardsInDeck;

	public Deck() {
		cardsInDeck = 0;
		this.cards = new ArrayDeque<>();
	}

	public void initializeDeck() {

		Color[] colors = Color.values();
		for (Color color : colors) {
			if (color == Color.WILD)
				continue;
			cards.addLast(new Card(color, Value.ZERO));
			cardsInDeck++;

			Value[] values = Value.values();
			for (Value value : values) {
				if (value == Value.ZERO)
					continue;

				cards.addLast(new Card(color, value));
				cardsInDeck++;
				cards.addLast(new Card(color, value));
				cardsInDeck++;
			}
		}

		for (int i = 0; i < 5; i++) {
			cards.addLast(new Card(Color.WILD, Value.NONE));
			cards.addLast(new Card(Color.WILD, Value.PLUS4));
		}

	}

	public void suffleCards() {
		int suffleTimes = new Random().nextInt(100) + 50;
		Card[] cardsArray = (Card[]) cards.toArray();
		for (int j = 0; j < suffleTimes; j++) {
			int x = cardsInDeck;
			int suffle = new Random().nextInt(x / 2);
			
			while(suffle < x) {
				Card temp = cardsArray[suffle];
				cardsArray[suffle] = cardsArray[x];
				cardsArray[x] = temp;
				suffle++;
				x--;
			}
		}

	}

	/* getter-setters */
	public Deque<Card> getCards() {
		return cards;
	}

	public void setCards(Deque<Card> cards) {
		this.cards = cards;
	}

	public int getCardsInDeck() {
		return cardsInDeck;
	}

	public void setCardsInDeck(int cardsInDeck) {
		this.cardsInDeck = cardsInDeck;
	}
}
