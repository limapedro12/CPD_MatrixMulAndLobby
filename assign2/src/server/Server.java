package server;

import java.io.*;
import java.net.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.security.KeyStore;

import server.lobby.*;

// Para conectar utiliza o client ou "openssl s_client -connect localhost:<port>""

public class Server {
    private static ServerSocket serverSocket;

    private static SimpleLobby simpleLobby = new SimpleLobby(3);
    private static RankLobby rankLobby = new RankLobby(3, 100, true);

    public static void main(String[] args) {
        if (args.length < 1) return;
 
        int port = Integer.parseInt(args[0]);

        start(port);
        listen();
    }

    private static void start(int port) {
        serverSocket = createSSLServerSocket(port);

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
            case "HELLO":   // HELLO <token>
                if (parts.length != 2) {
                    sendDirectMessage("ERROR: Token: Invalid token.", socket);
                    break;
                }
                player = Player.getPlayerByToken(parts[1], socket);

                if (player != null)
                    player.send("SUCCESS: Session restored.");
                else
                    sendDirectMessage("ERROR: Please authenticate.", socket);
            case "AUTH":    // AUTH <username> <password>
                if (parts.length != 3) {
                    sendDirectMessage("ERROR: Usage: AUTH <username> <password>", socket);
                    break;
                }
                player = Player.login(parts[1], parts[2], socket);

                if (player != null)
                    player.send("SUCCESS: Authenticated successfully. TOKEN = " + player.getToken());
                else 
                    sendDirectMessage("ERROR: Account does not exist.", socket);
                break;
            case "REGISTER":    // REGISTER <username> <password>
                if (parts.length != 3) {
                    sendDirectMessage("ERROR: Usage: REGISTER <username> <password>", socket);
                    break;
                }
                boolean registered = Player.register(parts[1], parts[2], socket);
                
                if (registered == false) 
                    sendDirectMessage("ERROR: Account already exists.", socket);
                else 
                    sendDirectMessage("Registered succesfully. Please log in.",socket);
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
                    player.send("SUCCESS: Player added to Simple Lobby");
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
                    player.send("SUCCESS: Player added to Rank Lobby");
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
                        player.send("SUCCESS: Player removed from Simple Lobby");
                        player.setState(PlayerState.IDLE);
                    } else if (player.getState() == PlayerState.RANK_LOBBY) {
                        rankLobby.removePlayer(player);
                        player.send("SUCCESS: Player removed from Rank Lobby");
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

    private static SSLServerSocket createSSLServerSocket(int port) {
        SSLServerSocket sslServerSocket;

        String working_dir = System.getProperty("user.dir");
        String keyFilePath = working_dir + "/server/certificate/keystore.jks";
        String keyPassword = "trabalhoCPD";

        KeyStore ks;
        KeyManagerFactory kmf;
        SSLContext sslc;
        

        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyFilePath), keyPassword.toCharArray());

            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassword.toCharArray());

            sslc = SSLContext.getInstance("TLS");
            sslc.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslc.getServerSocketFactory();
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

        } catch (Exception e) {
            System.out.println("Error creating SSL Server Socket: " + e.getMessage());
            return null;
        }

        return sslServerSocket;
    }
}

