package server.lobby;

import server.Player;
import server.Game;
import java.util.HashSet;

public class SimpleLobby implements Lobby {
    private int numPlayers;

    public SimpleLobby(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public synchronized void addPlayer(Player player) {
        playersWaiting.add(player);
        if(playersWaiting.size() == this.numPlayers) {
            new Thread(new Game(new HashSet<Player>(playersWaiting))).start();
            playersWaiting.clear();
        }
    }
}
