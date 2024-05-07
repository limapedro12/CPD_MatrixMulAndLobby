package server;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
 
public class Game {

    private List<Player> players;
    private Map<Player, Integer> guessDists = new HashMap<>();
    private int totalPlayers;

    public Game(List<Player> players) {
        this.players = players;
        for (Player player : players)
            guessDists.put(player, null);
        totalPlayers = players.size();
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
                    String answer = null;

                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < 30000) {
                        answer = player.receive();
                    }
                    if (System.currentTimeMillis() - start >= 30000) {
                        player.send("You were kicked of the game due to inactivity...");
                        player.setState(PlayerState.IDLE);
                        guessDists.remove(player);
                        it.remove();
                        break;
                    }

                    try {
                        guess = Integer.parseInt(answer);
                        if (guess < 0 || guess > 100) throw new IllegalArgumentException();
                    } catch (Exception e) {
                        player.send("Your guess is invalid. Please try again, making sure it is an integer between 0 and 100.");
                        guess = -1;
                    }
                }
                
                guessDists.put(player, Math.abs(guess-number));
            }

            notifyPlayers(generateRoundRank(number));

            Map.Entry<Player, Integer> entry = guessDists.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .orElse(null);
        
            if (entry != null) {
                Player last = entry.getKey();
                int points = totalPlayers - players.size();
                guessDists.remove(last);
                last.send("You lost. +" + points + "points for you!");
                players.remove(last);
                // last.incrementPoints(points);
            }
        }
        Player winner = players.get(0);
        winner.send("You won. Congratulations! +" + totalPlayers + "points for you!");
        // winner.incrementPoints(totalPlayers);
        guessDists.clear();
        players.clear();
    }

    private String generateRoundRank(int number) {
        StringBuilder ret = new StringBuilder();

        AtomicInteger index = new AtomicInteger(0);

        guessDists.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(entry -> {
                ret.append("#" + index.getAndIncrement() + ": " + entry.getKey().getUsername() + ", distance = " + entry.getValue() + "\n");
            });

        return ret.toString();
    }
}
