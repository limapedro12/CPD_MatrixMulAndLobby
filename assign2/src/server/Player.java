package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utils.Pair;

public class Player {
    // represents a Player from the perspective of the server

    private static Map<Pair<String, String>, Player> loggedPlayers = new HashMap<Pair<String, String>, Player>();
    private static Map<String, Player> playersByToken = new HashMap<String, Player>();

    private String currentToken;
    private Socket currentSocket;

    private String username;
    private String password;

    private int points = 0;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Player login(String username, String password, Socket socket) {
        Player player;
        if (loggedPlayers.containsKey(new Pair<String, String>(username, password))) {
            player = loggedPlayers.get(new Pair<String, String>(username, password));
        } else if (existsInDatabase(username, password)) {
            player = new Player(username, password);
            player.generateToken();
            loggedPlayers.put(new Pair<String, String>(username, password), player);
            playersByToken.put(player.getToken(), player);
        } else return null;

        player.currentSocket = socket;

        return player;
    }

    public static Player getPlayerByToken(String token) {
        return playersByToken.get(token);
    }

    public static void logout(Player player) {
        loggedPlayers.remove(new Pair<String, String>(player.username, player.password));
    }

    private void generateToken() {
        this.currentToken = this.username; //+ Integer.toString((int) (Math.random() * 1000000));
    }

    public String getToken() {
        return this.currentToken;
    }

    private static boolean existsInDatabase(String username, String password) {
        try {
            File file = new File("/media/dotw/The Big One/trabalhos(2)/FEUP/3o_ano_2o_semestre/CPD/g17/assign2/src/server/storage/players.csv");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                String storedUsername = data[0];
                String storedPassword = data[1];
                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    scanner.close();
                    return true;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: players.csv file not found.");
        }
        return false;
    }

    public void incrementPoints(int inc) {
        this.points += inc;
    }

    public void decrementPoints(int dec) {
        this.points = Math.max(0, this.points-dec);
    }

    public void send(String message) {
        try {
            OutputStream output = this.currentSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(message);
        } catch (IOException ex) {
            System.out.println("Cannot send message to player " + this.username);
        }
    }

    public Socket getSocket() {
        return this.currentSocket;
    }
}
