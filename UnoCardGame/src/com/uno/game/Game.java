package com.uno.game;

import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;

public class Game {

	private Deck flippedDeck;
	private Deck discardedDeck;
	private Card previousCard;

	private Map<Integer, Player> players;
	private int nextPlayer;
	private boolean direction;

	private boolean penalty;

	public Game(Map<Integer, Player> players) {
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

	private void startGame() {
		flippedDeck.suffleCards();
		distributeCards();
		showCards();
	}

	private void distributeCards() {
		for (Map.Entry<Integer, Player> e : players.entrySet()) {
			Player player = e.getValue();
			LinkedList<Card> playerHand = player.getPlayerHand();
			for (int i = 0; i < 7; i++) {
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

		System.out.println("+-------------------------------------------------------------------------------+");
		System.out.println("|	**Card at the top : " + previousCard + "**	  		|");
		
		Player player = players.get(nextPlayer);
		System.out.println("|		  ***Turn : " + player + "***	  	  		|");
		System.out.println("         ____________________________________________________   ");
		System.out.println();
		
		LinkedList<Card> playerHand = player.getPlayerHand();

		if (penalty) {
			applyPenalty();
			printEndingLines();
			findNextPlayer();
			showCards();
			return;
		}

		int suitableCards = 0;
		int cardNo = 1;
		for (Card card : playerHand) {
			System.out.println("	" +cardNo + " : " + card);
			cardNo++;
			if (cardSuitability(card))
				suitableCards++;
		}

		if (suitableCards == 0) {
			takeCards(1);
			System.out.println("	No suitable Card found to play! Added one Card and skipped Turn!!");
			
			printEndingLines();
			findNextPlayer();
			
			showCards();
			return;
		}

		turn();

	}

	

	public void turn() {
		
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();

		System.out.println();
		System.out.println("Enter Card number to play [1-" + playerHand.size() + "] : ");
		Scanner scanner = new Scanner(System.in);
		int cardNo = scanner.nextInt();

		if (cardNo > playerHand.size() || cardNo <= 0) {
			System.err.println("Invalid input : " + cardNo);
			turn();
		}

		Card card = playerHand.get(cardNo - 1);

		if (!cardSuitability(card)) {
			System.err.println("Invalid Card selected! Top Card : " + previousCard);
			turn();
			return;
		}

		playerHand.remove(cardNo - 1);
		discardedDeck.getCards().add(card);
		discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

		if(playerHand.isEmpty()) {
			System.out.println("___________________________________________");
			System.out.println();
			System.out.println("|   Winner is : "+ player +"   |");
			System.out.println("___________________________________________");
			System.exit(0);
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
		}else if (previousCard.getValue() == Value.SKIP) {
			findNextPlayer();
		}

		findNextPlayer();
		printEndingLines();
		
		showCards();
	}

	private void chooseColor() {

		System.out.println("+======================+");
		System.out.println("|Choose color [1-4] :	|");
		System.out.println("|	1. RED		|");
		System.out.println("|	2. BLUE		|");
		System.out.println("|	3. GREEN	|");
		System.out.println("|	4. YELLOW	|");

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
			System.err.println("Invalid input : " + color);
			chooseColor();
			return;
		}
		
		System.out.println("+======================+");
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
		
		if(flippedDeck.getCards().size() < noOfCards) {
			while(!flippedDeck.getCards().isEmpty()) {
				discardedDeck.getCards().push(flippedDeck.getCards().pop());
				flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
				discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);
			}
			flippedDeck = discardedDeck;
			flippedDeck.suffleCards();
			discardedDeck = new Deck();
			
			Card firstCard = flippedDeck.getCards().peek();
			while (firstCard.getColor() == Color.WILD) {
				flippedDeck.suffleCards();
				firstCard = flippedDeck.getCards().peek();
			}

			discardedDeck.getCards().push(firstCard);
			flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
			discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

			previousCard = discardedDeck.getCards().peek();
			 
		}
		
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
			System.out.println("	Added 2 Cards and skipped Turn!");
		} else if (previousCard.getValue() == Value.PLUS4) {
			takeCards(4);
			System.out.println("	Added 4 Cards and skipped Turn!");
		}
		penalty = false;
	}

	private void printEndingLines() {
		System.out.println("|                                                                               |");
		System.out.println("|                                                                               |");
		System.out.println("+-------------------------------------------------------------------------------+");
	}
	
}
