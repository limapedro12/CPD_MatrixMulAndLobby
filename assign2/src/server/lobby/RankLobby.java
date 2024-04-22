package server.lobby;

import server.Player;

public class RankLobby implements Lobby {
    public void addPlayer(Player player) {
        playersWaiting.add(player);
        // TODO
    }
}
