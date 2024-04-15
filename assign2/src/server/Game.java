package server;
<<<<<<< HEAD
=======

import java.net.*;
import java.util.List;
>>>>>>> f2d646856b9f9cafb321c9138c80e592d5d51aa5

import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This program demonstrates a simple TCP/IP socket server.
 *
 * @author www.codejava.net
 */
public class Game {

    private List<Socket> userSockets;

    public Game(int players, List<Socket> userSockets) {
        this.userSockets = userSockets;
    }
 
    public void startServer(int port) {
        System.out.println("Starting game with " + userSockets.size() + " players");
 
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