package server;

import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Game {

    private List<Player> players;
    private GameThread thread;

    public Game(List<String> userTokens) {
        this.userTokens = userTokens;
    }
}