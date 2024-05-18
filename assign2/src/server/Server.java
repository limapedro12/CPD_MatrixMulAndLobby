package server;

import java.io.*;
import java.net.*;

import server.lobby.*;

public class Server {
    private static ServerSocket serverSocket;

    private static SimpleLobby simpleLobby;
    private static RankLobby rankLobby;

    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);
        int numPlayers = Integer.parseInt(args[1]);

        simpleLobby = new SimpleLobby(numPlayers);
        rankLobby = new RankLobby(numPlayers, 100, true);

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

        Thread.ofVirtual().start(rankLobby);
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
            case "HELLO":   // HELLO <token>
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Token: Invalid token.", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player != null)
                    player.send("Session restored.");
                else
                    sendDirectMessage("ERROR: Please authenticate.", socket);
            case "AUTH":    // AUTH <username> <password>
                if (parts.length != 3) {
                    sendDirectMessage("ERROR: Usage: AUTH <username> <password>", socket);
                    break;
                }
                player = Player.login(parts[1], parts[2], socket);

                if (player != null)
                    player.send("Authenticated successfully.\nTOKEN = " + player.getToken());
                else 
                    sendDirectMessage("ERROR: Account does not exist.", socket);
                break;
            case "REGISTER":    // REGISTER <username> <password>
                if (parts.length != 3) {
                    sendDirectMessage("ERROR: Usage: REGISTER <username> <password>", socket);
                    break;
                }
                player = Player.login(parts[1], parts[2], socket);

                if (player == null) 
                    sendDirectMessage("ERROR: Account already exists.", socket);
                else 
                    player.send("Registered succesfully. Please log in.");
                break;
            case "SIMPLE":  // SIMPLE <token>
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Usage: SIMPLE <token>", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player == null)
                    sendDirectMessage("ERROR: Account does not exist.", socket);
                else if(player.getState() == PlayerState.IDLE){
                    simpleLobby.addPlayer(Player.getPlayerByToken(parts[1], socket));
                    player.send("Player added to Simple Lobby");
                } else
                    player.send("ERROR: Player already in " + player.getState());
                break;
            case "RANK":    // RANK <token>
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Usage: RANK <token>", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player == null)
                    sendDirectMessage("ERROR: Account does not exist.", socket);
                else if(player.getState() == PlayerState.IDLE){
                    System.out.println("Player added to Rank Lobby");
                    rankLobby.addPlayer(Player.getPlayerByToken(parts[1], socket));
                    player.send("Player added to Rank Lobby");
                } else
                    player.send("ERROR: Player already in " + player.getState());
                break;
            case "LEAVE_LOBBY":
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Usage: LEAVE_LOBBY <token>", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player != null) {
                    if (player.getState() == PlayerState.SIMPLE_LOBBY) {
                        simpleLobby.removePlayer(player);
                        player.send("Player removed from Simple Lobby");
                        player.setState(PlayerState.IDLE);
                    } else if (player.getState() == PlayerState.RANK_LOBBY) {
                        rankLobby.removePlayer(player);
                        player.send("Player removed from Rank Lobby");
                        player.setState(PlayerState.IDLE);
                    } else
                        player.send("ERROR: Player not in a lobby");
                } else
                    sendDirectMessage("ERROR: Token: Invalid token.", socket);
                break;
            case "POINTS":  // POINTS <token>
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Usage: POINTS <token>", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player != null)
                    player.send("You have " + player.getPoints() + " points.");
                else
                    sendDirectMessage("ERROR: Token: Invalid token.", socket);
                break;
            case "PLAY":    // PLAY <token> <guess>
                if (parts.length != 3) {
                    sendDirectMessage("ERROR: Usage: PLAY <token> <guess>", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player != null && player.getState() == PlayerState.GAME)
                    player.setLastMessage(parts[2]);
                else
                    sendDirectMessage("ERROR: Token: Invalid token.", socket);
                break;
            default:
                sendDirectMessage("ERROR: Command: Invalid command.", socket);
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
