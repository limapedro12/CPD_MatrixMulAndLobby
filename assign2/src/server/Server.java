package server;

import java.io.*;
import java.net.*;
import java.util.*;

import server.lobby.*;

public class Server {
    private static ServerSocket serverSocket;

    private static SimpleLobby simpleLobby = new SimpleLobby(3);
    private static RankLobby rankLobby = new RankLobby();

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
    }

    private static void listen() {
        Thread.ofVirtual().start(() -> listenToSockets());
        while (true) {
            try {
                Socket socket = serverSocket.accept(); // blocks until a connection is made

                synchronized (userSockets) {
                    userSockets.add(socket);
                }

            } catch (IOException e) {
                System.out.println("Error accepting socket connection: " + e.getMessage());
            }
        }
    }

    private static void listenToSockets() {
        while (true) {
            int i = -1;
            int size;
            synchronized (userSockets) {
                size = userSockets.size();
            }
            try {
                for (i = 0; i < size; i++) {
                    Socket socket;
                    synchronized (userSockets) {
                        socket = userSockets.get(i);
                    }
                    synchronized (socket) {
                        InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                        String message = reader.ready() ? reader.readLine() : null;
                        if (message != null) {
                            handleMessage(message, socket);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading message " + Integer.toString(i) + ": " + e.getMessage());
                break;
            }
        }
    }

    private static void handleMessage(String message, Socket socket) {
        String[] parts = message.split(" ");

        if (parts.length < 2) return;

        String command = parts[0];

        Player player = null;

        switch (command) {
            case "AUTH":    // AUTH <username> <password>
                player = Player.login(parts[1], parts[2], socket);

                if (player != null)
                    player.send("Authenticated successfully.\n TOKEN = " + player.getToken());
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
                Player possiblePlayer = Player.getPlayerByToken(parts[1]);
                if(possiblePlayer != null){
                    simpleLobby.addPlayer(possiblePlayer);
                    possiblePlayer.send("You have been added to the simple lobby.");
                } else
                    sendDirectMessage("Invalid token.", socket);
                break;
            case "RANK":    // RANK <token>
                rankLobby.addPlayer(Player.getPlayerByToken(parts[1]));
                break;
            default:
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
