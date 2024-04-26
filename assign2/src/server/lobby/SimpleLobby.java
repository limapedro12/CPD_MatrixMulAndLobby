package server.lobby;

import server.Player;

public class SimpleLobby implements Lobby {
    public void addPlayer(Player player) {
        playersWaiting.add(player);
        if(playersWaiting.size() == 2) {
            notifyPlayers("Game is starting!");
            playersWaiting.clear();
        }
    }
}
