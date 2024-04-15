package server;

import java.net.*;
import java.util.List;

public class Game {
    private List<Socket> userSockets;

    public Game(int players, List<Socket> userSockets) {
        this.userSockets = userSockets;
    }

    public static void main(String[] args) {
        // Code to start the game
        //System.out.println("Starting game with " + userSockets.size() + " players");
        System.out.println("Hello from Server!");
        Game2.message();
        // ...
    }
}
