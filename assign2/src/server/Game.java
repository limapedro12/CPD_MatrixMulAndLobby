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
        for(Player player : players) {
            player.setState(PlayerState.GAME);
        }
    }

    public void run() {
        System.out.println("Game started!");

        for (Player player : players) {
            player.send("Welcome to the game!");
            player.send("Goodbye!");
        }

        for (Player player : players) {
            player.setState(PlayerState.IDLE);
        }
    }
}