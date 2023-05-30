package com.uno.game;

import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;
import com.uno.game.exceptions.InvalidCard;

public class Game {

	private Deck flippedDeck;
	private Deck discardedDeck;
	private Card previousCard;

	private Map<Integer, Player> players;
	private int nextPlayer;
	private boolean direction;

	private boolean penalty;

	public Game(Map<Integer, Player> players) throws InvalidCard {
		this.flippedDeck = new Deck();
		this.discardedDeck = new Deck();
		this.previousCard = null;
		this.players = players;
		this.nextPlayer = 1;
		this.direction = true;
		this.penalty = false;
		flippedDeck.initializeDeck();
		startGame();
	}

	private void startGame() throws InvalidCard {
		;
		flippedDeck.suffleCards();
		distributeCards();
		showCards();
	}

	private void distributeCards() {
		for (Map.Entry<Integer, Player> e : players.entrySet()) {
			Player player = e.getValue();
			LinkedList<Card> playerHand = player.getPlayerHand();
			for (int i = 0; i < 3; i++) {
				playerHand.add(flippedDeck.getCards().pop());
				flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
			}
		}
		/* one discarded card from flipped cards */
		Card firstCard = flippedDeck.getCards().peek();
		while (firstCard.getColor() == Color.WILD) {
			flippedDeck.suffleCards();
			firstCard = flippedDeck.getCards().peek();
		}

		discardedDeck.getCards().push(firstCard);
		flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
		discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

		previousCard = discardedDeck.getCards().peek();

		if (previousCard.getValue() == Value.REVERSE) {
			nextPlayer = players.size();
			direction = false;
		} else if (previousCard.getValue() == Value.SKIP)
			nextPlayer++;
		else if (previousCard.getValue() == Value.PLUS2) {
			penalty = true;
		}

	}

	public void showCards() {

		System.out.println("Card at the top : " + previousCard);
		System.out.println("===============================================");
		Player player = players.get(nextPlayer);
		System.out.println("Turn : " + player);
		LinkedList<Card> playerHand = player.getPlayerHand();

		if (penalty) {
			applyPenalty();
			findNextPlayer();
			showCards();
			return;
		}

		int suitableCards = 0;
		int cardNo = 1;
		for (Card card : playerHand) {
			System.out.println(cardNo + " : " + card);
			cardNo++;
			if (cardSuitability(card))
				suitableCards++;
		}

		if (suitableCards == 0) {
			takeCards(1);
			System.err.println("No suitable Card found to play! Added one Card and skipped turn!!");
			findNextPlayer();
			showCards();
			return;
		}

		turn();

	}

	

	public void turn() {
		
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();

		System.out.println("Enter Card Number to play [1-" + playerHand.size() + "] : ");
		Scanner scanner = new Scanner(System.in);
		int cardNo = scanner.nextInt();

		if (cardNo > playerHand.size() || cardNo <= 0) {
			System.err.println("Invalid Input : " + cardNo);
			turn();
		}

		Card card = playerHand.get(cardNo - 1);

		if (!cardSuitability(card)) {
			System.err.println("Invalid card selected : " + card);
			turn();
		}

		playerHand.remove(cardNo - 1);
		discardedDeck.getCards().add(card);
		discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

		if(playerHand.isEmpty()) {
			System.out.println("________________________________");
			System.out.println();
			System.out.println("|   Winner is : "+ player +"   |");
			System.out.println("________________________________");
			return;
		}
		
		previousCard = card;

		if (previousCard.getColor() == Color.WILD) {
			chooseColor();
		}

		if (previousCard.getValue() == Value.REVERSE) {
			if (direction)
				direction = false;
			else
				direction = true;
		}else if (previousCard.getValue() == Value.PLUS2 || previousCard.getValue() == Value.PLUS4) {
			penalty = true;
		}

		findNextPlayer();

		showCards();
	}

	private void chooseColor() {

		System.out.println("Choose color [1-4] : ");
		System.out.println("1. RED");
		System.out.println("2. BLUE");
		System.out.println("3. GREEN");
		System.out.println("4. YELLOW");

		Scanner scanner = new Scanner(System.in);
		int color = scanner.nextInt();

		if (color == 1) {
			previousCard.setColor(Color.RED);
		} else if (color == 2) {
			previousCard.setColor(Color.BLUE);
		} else if (color == 3) {
			previousCard.setColor(Color.GREEN);
		} else if (color == 4){
			previousCard.setColor(Color.YELLOW);
		} else {
			System.err.println("Invalid Input : " + color);
			chooseColor();
		}

	}

	private boolean cardSuitability(Card card) {

		if (card.getColor() == Color.WILD || card.getColor() == previousCard.getColor()) {
			return true;
		} else if (card.getValue() == previousCard.getValue()) {
			if (card.getValue() != Value.PLUS2 && card.getValue() != Value.SKIP && card.getValue() != Value.REVERSE) {
				return true;
			}
		}
		return false;
	}

	private void takeCards(int noOfCards) {
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();
		for (int i = 0; i < noOfCards; i++) {
			playerHand.add(flippedDeck.getCards().pop());
			flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
		}
	}

	private void findNextPlayer() {
		
		if (direction == true) {
			nextPlayer++;
			if (nextPlayer > players.size())
				nextPlayer = 1;
		} else {
			nextPlayer--;
			if (nextPlayer == 0)
				nextPlayer = players.size();
		}
	}
	
	private void applyPenalty() {
		if (previousCard.getValue() == Value.PLUS2) {
			takeCards(2);
			System.err.println("Added 2 cards and Skipped turn!");
		} else if (previousCard.getValue() == Value.PLUS4) {
			takeCards(4);
			System.err.println("Added 4 cards and Skipped turn!");
		}
		penalty = false;
	}

}
