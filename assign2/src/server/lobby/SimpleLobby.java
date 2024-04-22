package server.lobby;

import server.Player;

public class SimpleLobby implements Lobby {
    public void addPlayer(Player player) {
        playersWaiting.add(player);
        // TODO
    }
}
