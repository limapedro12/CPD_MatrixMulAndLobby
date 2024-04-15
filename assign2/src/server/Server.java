package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);

        Game game = new Game(new ArrayList<String>());

        game.startServer(port);
    }

    public void startServer(int port) {
        System.out.println("Starting game with " + userTokens.size() + " players");
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();

                GameThread thread = new GameThread(socket);
                thread.start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
