package com.uno;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.uno.game.Game;
import com.uno.game.Player;
import com.uno.game.exceptions.InvalidCard;

public class Runner {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter number of players [2-10]: ");
		int noOfPlayers = scanner.nextInt();
		
		if(noOfPlayers > 10 || noOfPlayers < 2) {
			System.err.println("Invalid Input : " + noOfPlayers);
			main(args);
		}
		
		Map<Integer, Player> players = new HashMap<>();
		
		for(int i=1; i<=noOfPlayers; i++) {
			System.out.println("Enter Player " + i + " Name : ");
			String name = scanner.next();
			
			players.put(1, new Player(i, name));
		}
		
		try {
			Game game = new Game(players);
		} catch (InvalidCard e) {
			System.err.println(e.getMessage());
		}
		
	}
	
}
