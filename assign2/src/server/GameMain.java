package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameMain {
    public static void main(String[] args) {
        if (args.length < 2) return;
 
        int port = Integer.parseInt(args[0]);
        int numPlayers = Integer.parseInt(args[1]);

        Game game = new Game(numPlayers, new ArrayList<Socket>());

        game.startServer(port);
    }
}
