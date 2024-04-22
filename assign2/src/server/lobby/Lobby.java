package server.lobby;

import java.util.HashSet;
import java.util.Set;

import server.Player;

public interface Lobby {
    Set<Player> playersWaiting = new HashSet<>();

    public void addPlayer(Player player);

    public default void notifyPlayers(String message) {
        for (Player player : playersWaiting)
            player.send(message);
    }
}
