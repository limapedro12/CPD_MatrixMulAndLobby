package server;

import java.util.*;
 
public class Game {

    private List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public static void run(List<Player> players) {
        Game game = new Game(players);
        game.play();
    }

    private void notifyPlayers(String message) {
        for (Player player : players)
            player.send(message);
    }

    private void play() {
        notifyPlayers("Welcome!");

        while (players.size() > 1) {
            notifyPlayers(players.size() + " players remaining. Please wait for your turn.");

            int number = (int) Math.random() * 101;

            Iterator<Player> it = players.iterator();
            while (it.hasNext()) {
                Player player = it.next();
                int guess = -1;
                player.send("Place your guess as an integer between 0 and 100.");

                while (guess == -1) {
                    // TODO: tempo limite para receber
                    // player.send("You were kicked of the game due to inactivity.");
                    // player.changeState(not playing);
                    // it.remove();
                    // break;
                    String answer = player.receive();
                    try {
                        guess = Integer.parseInt(answer);
                        if (guess < 0 || guess > 100) throw new IllegalArgumentException();
                    } catch (Exception e) {
                        player.send("Your guess is invalid. Please try again, making sure it is an integer between 0 and 100.");
                        guess = -1;
                    }
                }
                player.setGuessDist(Math.abs(guess-number));
            }

            notifyPlayers(generateRoundRank(number));

            Player last = players.getLast();
            last.send("You terminated in #" + players.size() + ".");
            // last.changeState(not playing);
            players.removeLast();
        }
        players.get(0).send("You won. Congratulations!");
    }

    private String generateRoundRank(int number) {
        StringBuilder ret = new StringBuilder();
        ret.append("1");

        Collections.sort(players);

        Player player;

        for (int i = 0; i < players.size() - 1; i++) {
            player = players.get(i);
            ret.append("#" + (i+1) + ": " + player.getUsername() + ", distance = " + player.getGuessDist() + "\n");
        }
        player = players.getLast();
        ret.append("#" + players.size() + ": " + player.getUsername() + ", distance = " + player.getGuessDist() + "\n");

        return ret.toString();
    }
}
