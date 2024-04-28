package server;

import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Game implements Runnable {

    private Set<Player> players;

    public Game(Set<Player> players) {
        this.players = players;
    }

    public void run() {
        for (Player player : players) {
            try {
                PrintWriter writer = new PrintWriter(player.getSocket().getOutputStream(), true);
                Scanner scanner = new Scanner(player.getSocket().getInputStream());

                writer.println("Welcome to the game!");

                while (true) {
                    writer.println("Enter a number: ");
                    String input = scanner.nextLine();
                    if (input.equals("quit")) {
                        players.remove(player);
                        break;
                    }
                    writer.println("You entered: " + input);
                }

                writer.println("Goodbye!");

                scanner.close();
                writer.close();
            } catch (IOException e) {
                System.out.println("Error handling player: " + e.getMessage());
            }
        }
    }
}