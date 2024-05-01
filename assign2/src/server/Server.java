package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.lobby.*;

public class Server {
    private static ServerSocket serverSocket;

    private static SimpleLobby simpleLobby = new SimpleLobby(3);
    private static RankLobby rankLobby = new RankLobby(3, 100, true);

    private static List<Socket> userSockets = new ArrayList<Socket>();

    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);

        start(port);
        listen();
    }

    private static void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            return;
        }

        System.out.println("Server is online on port " + port + "!");

        new Thread(rankLobby).start();
    }

    private static void listen() {
        while (true) {
            try {
                Socket socket = serverSocket.accept(); // blocks until a connection is made

                Thread.ofVirtual().start(() -> listenToSocket(socket));

            } catch (IOException e) {
                System.out.println("Error accepting socket connection: " + e.getMessage());
            }
        }
    }

    private static void listenToSocket(Socket socket) {
        while (true) {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String message = reader.readLine();
                if (message != null) {
                    handleMessage(message, socket);
                }
            
            } catch (IOException e) {
                System.out.println("Error reading message: " + e.getMessage());
                break;
            }
        }
    }

    private static void handleMessage(String message, Socket socket) {
        String[] parts = message.split(" ");

        if (parts.length < 2) {
            sendDirectMessage("Invalid command.", socket);
            return;
        }

        String command = parts[0];

        Player player = null;

        switch (command) {
            case "AUTH":    // AUTH <username> <password>
                player = Player.login(parts[1], parts[2], socket);

                if (player != null)
                    player.send("Authenticated successfully.\nTOKEN = " + player.getToken());
                else 
                    sendDirectMessage("Account does not exist.", socket);
                break;
            case "REGISTER":    // REGISTER <username> <password>
                player = Player.login(parts[1], parts[2], socket);
                if (player == null) 
                    sendDirectMessage("Account already exists.", socket);
                else 
                    player.send("Registered succesfully. Please log in.");
                break;
            case "SIMPLE":  // SIMPLE <token>
                player = Player.getPlayerByToken(parts[1], socket);

                if (player == null)
                    sendDirectMessage("Account does not exist.", socket);
                else if(player.getState() == PlayerState.IDLE){
                    simpleLobby.addPlayer(Player.getPlayerByToken(parts[1], socket));
                    player.send("Player added to Simple Lobby");
                } else
                    player.send("Player already in " + player.getState());
                break;
            case "RANK":    // RANK <token>
                player = Player.getPlayerByToken(parts[1], socket);

                if (player == null)
                    sendDirectMessage("Account does not exist.", socket);
                else if(player.getState() == PlayerState.IDLE){
                    rankLobby.addPlayer(Player.getPlayerByToken(parts[1], socket));
                    player.send("Player added to Rank Lobby");
                } else
                    player.send("Player already in " + player.getState());
                break;
            case "POINTS":
                player = Player.getPlayerByToken(parts[1], socket);
                if (player != null) {
                    player.send("You have " + player.getPoints() + " points.");
                } else {
                    sendDirectMessage("Invalid token.", socket);
                }
                break;
            default:
                sendDirectMessage("Invalid command.", socket);
                break;
        }
    }

    private static void sendDirectMessage(String message, Socket socket) {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
