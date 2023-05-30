package com.uno.game;

import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import com.uno.game.enums.Color;
import com.uno.game.enums.Value;

public class Game {

	/*(Flipped) Unknown cards pile*/
	private Deck flippedDeck;
	
	/*(Faced) Played cards pile*/
	private Deck discardedDeck;
	
	/*Previously played card*/
	private Card previousCard;

	/*Players of the game*/
	private Map<Integer, Player> players;
	
	/*Saving next player to quickly fetch the details*/
	private int nextPlayer;
	
	/*To detect next player to play the card (clockwise or anti-clockwise*/
	private boolean direction;

	/*For penalty cards (Special cards)*/
	private boolean penalty;

	public Game(Map<Integer, Player> players) {
		this.flippedDeck = new Deck();
		this.discardedDeck = new Deck();
		this.previousCard = null;
		this.players = players;
		this.nextPlayer = 1;
		this.direction = true;
		this.penalty = false;
		
		/*To get all the cards inside the flipped desk*/
		flippedDeck.initializeDeck();
		
		startGame();
	}

	/*To initialize the game*/
	private void startGame() {
		
		/*Shuffling the cards*/
		flippedDeck.shuffleCards();
		
		distributeCards();
		
		/*First turn*/
		showCards();
		
	}

	/*To distribute the cards among the players and setting up the game*/
	private void distributeCards() {
		
		/*Distributing 7 cards to each player*/
		for (Map.Entry<Integer, Player> e : players.entrySet()) {
			Player player = e.getValue();
			LinkedList<Card> playerHand = player.getPlayerHand();
			for (int i = 0; i < 7; i++) {
				playerHand.add(flippedDeck.getCards().pop());
				flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
			}
		}
		
		/* Selecting topmost card from flipped cards
		 * and adding that card in discarded deck to initialize the game*/
		Card firstCard = flippedDeck.getCards().peek();
		
		while (firstCard.getColor() == Color.WILD) {
			flippedDeck.shuffleCards();
			firstCard = flippedDeck.getCards().peek();
		}

		discardedDeck.getCards().push(firstCard);
		flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
		discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

		previousCard = discardedDeck.getCards().peek();

		/*setting up the game according to the discarded card*/
		if (previousCard.getValue() == Value.REVERSE) {
			nextPlayer = players.size();
			direction = false;
		} else if (previousCard.getValue() == Value.SKIP)
			nextPlayer++;
		else if (previousCard.getValue() == Value.PLUS_2) {
			penalty = true;
		}

	}

	/*Showing up cards and performing up-front verification and automated play*/
	public void showCards() {

		System.out.println("+-------------------------------------------------------------------------------+");
		System.out.println("|	**Card at the top : " + previousCard + "**	  		|");
		
		/*Extracting current player*/
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();
		
		System.out.println("|		  ***Turn : " + player + "***	  	  		|");
		System.out.println("         ____________________________________________________   ");
		System.out.println();

		/*Checking if penalty(take cards from the flipped deck) is applicable to current player
		 * according to previously played card
		 * if yes, (automated) player will be constrained to pick up the required cards (2 or 4 accordingly)
		 * and skipping the turn*/
		if (penalty) {
			applyPenalty();
			printEndingLines();
			findNextPlayer();
			showCards();
			return;
		}

		/*Checking is suitable cards present or not*/
		int suitableCards = 0;
		int cardNo = 1;
		for (Card card : playerHand) {
			System.out.println("	" +cardNo + " : " + card);
			cardNo++;
			if (cardSuitability(card))
				suitableCards++;
		}

		/*if not a single compatible card present in the players hand
		 * according to the previously played card
		 * then player is constrained to pickup a card from flipped deck of cards (automated)
		 * and skipping the turn*/
		if (suitableCards == 0) {
			takeCards(1);
			System.out.println("	No suitable Card found to play! Added one Card and skipped Turn!!");
			
			printEndingLines();
			findNextPlayer();
			
			/*moving on to the next player*/
			showCards();
			return;
		}

		turn();

	}

	/*To play the turn of a particular player*/
	public void turn() {
		
		/*Extracting current player*/
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();

		System.out.println();
		System.out.println("Enter Card number to play [1-" + playerHand.size() + "] : ");
		
		Scanner scanner = new Scanner(System.in);
		int cardNo = scanner.nextInt();

		if (cardNo > playerHand.size() || cardNo <= 0) {
			System.err.println("	Invalid input : " + cardNo);
			turn();
			return;
		}

		/*Fetching the selected card*/
		Card card = playerHand.get(cardNo - 1);

		/*Verifying the compatibility of current card with the previously played card*/
		if (!cardSuitability(card)) {
			System.err.println("	Invalid Card selected! Top Card : " + previousCard);
			turn();
			return;
		}

		/*PLaying card: removing from player's hand(linked list) to discarded deck of the cards*/
		playerHand.remove(cardNo - 1);
		discardedDeck.getCards().add(card);
		discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

		/*Declaring winner if all cards have been spent*/
		if(playerHand.isEmpty()) {
			System.out.println("___________________________________________");
			System.out.println();
			System.out.println("|   Winner is : "+ player +"   |");
			System.out.println("___________________________________________");
			System.exit(0);
			return;
		}
		
		previousCard = card;

		/*In case player played wild card or wild card +4*/
		if (previousCard.getColor() == Color.WILD) {
			chooseColor();
		}

		if (previousCard.getValue() == Value.REVERSE) {
			if (direction)
				direction = false;
			else
				direction = true;
		}else if (previousCard.getValue() == Value.PLUS_2 || previousCard.getValue() == Value.PLUS_4) {
			penalty = true;
		}else if (previousCard.getValue() == Value.SKIP) {
			findNextPlayer();
		}

		findNextPlayer();
		printEndingLines();
		
		/*again for next player's turn (automated)*/
		showCards();
	}

	/*Choosing color after playing wild card*/
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

	/*To check the current card's compatibility with the previously played card*/
	private boolean cardSuitability(Card card) {

		if (card.getColor() == Color.WILD || card.getColor() == previousCard.getColor()) {
			return true;
		} else if (card.getValue() == previousCard.getValue()) {
			if (card.getValue() != Value.PLUS_2 && card.getValue() != Value.SKIP && card.getValue() != Value.REVERSE) {
				return true;
			}
		}
		return false;
	}

	/*To add cards to player's hand from the flipped deck cards*/
	private void takeCards(int noOfCards) {
		
		/*In case flipped deck is running out of required cards
		 * rearrange discarded cards and shuffle them*/
		if(flippedDeck.getCards().size() < noOfCards) {
			while(!flippedDeck.getCards().isEmpty()) {
				discardedDeck.getCards().push(flippedDeck.getCards().pop());
				flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
				discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);
			}
			flippedDeck = discardedDeck;
			flippedDeck.shuffleCards();
			discardedDeck = new Deck();
			
			/*again adding a card to discarded deck to initialize the game*/
			Card firstCard = flippedDeck.getCards().peek();
			while (firstCard.getColor() == Color.WILD) {
				flippedDeck.shuffleCards();
				firstCard = flippedDeck.getCards().peek();
			}

			discardedDeck.getCards().push(firstCard);
			flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
			discardedDeck.setCardsInDeck(discardedDeck.getCardsInDeck() + 1);

			previousCard = discardedDeck.getCards().peek();
			 
		}
		
		/*Adding cards from flipped deck to player's hand*/
		Player player = players.get(nextPlayer);
		LinkedList<Card> playerHand = player.getPlayerHand();
		for (int i = 0; i < noOfCards; i++) {
			playerHand.add(flippedDeck.getCards().pop());
			flippedDeck.setCardsInDeck(flippedDeck.getCardsInDeck() - 1);
		}
	}

	/*To find next player*/
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
	
	/*Wild Card +4 special card and Color +2 special card*/
	private void applyPenalty() {
		if (previousCard.getValue() == Value.PLUS_2) {
			takeCards(2);
			System.out.println("	Added 2 Cards and skipped Turn!");
		} else if (previousCard.getValue() == Value.PLUS_4) {
			takeCards(4);
			System.out.println("	Added 4 Cards and skipped Turn!");
		}
		penalty = false;
	}

	/*Just for UI, printing purpose*/
	private void printEndingLines() {
		System.out.println("|                                                                               |");
		System.out.println("|                                                                               |");
		System.out.println("+-------------------------------------------------------------------------------+");
	}
	
}
