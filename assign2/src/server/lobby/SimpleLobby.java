package server.lobby;

import server.Player;
import server.Game;

public class SimpleLobby implements Lobby {
    private int numPlayers;

    public SimpleLobby(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void addPlayer(Player player) {
        playersWaiting.add(player);
        if(playersWaiting.size() == this.numPlayers) {
            new Thread(new Game(playersWaiting)).start();
            playersWaiting.clear();
        }
    }
}
