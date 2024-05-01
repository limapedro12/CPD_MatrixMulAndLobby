package server.lobby;

import server.Player;
import server.PlayerState;
import server.Game;
import java.util.HashSet;

public class SimpleLobby implements Lobby {
    private int numPlayers;
    private HashSet<Player> playersWaiting;

    public SimpleLobby(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public synchronized void addPlayer(Player player) {
        player.setState(PlayerState.SIMPLE_LOBBY);
        playersWaiting = new HashSet<>();
        playersWaiting.add(player);
        if(playersWaiting.size() == this.numPlayers) {
            new Thread(new Game(new HashSet<Player>(playersWaiting))).start();
            playersWaiting.clear();
        }
    }
}
