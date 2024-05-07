package server.lobby;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

import server.Game;
import server.Player;
import server.PlayerState;

import utils.Pair;
import utils.Triplet;

public class RankLobby implements Runnable, Lobby {
    private int step;
    private int numPlayers;
    private boolean show;

    private List<Player> playersWaiting;
    private List<Integer> timeWaiting;

    ReentrantLock lock = new ReentrantLock();

    public RankLobby(int numPlayers, int step) {
        this.numPlayers = numPlayers;
        this.step = step;
        this.show = false;

        this.playersWaiting = new ArrayList<Player>();
        this.timeWaiting = new ArrayList<Integer>();
    }

    public RankLobby(int numPlayers, int step, boolean show) {
        this(numPlayers, step);
        this.show = show;
        if(show) System.out.println("Rank Lobby created with " + numPlayers + " players and step " + step);
    }

    public void addPlayer(Player player) {
        player.setState(PlayerState.RANK_LOBBY);
        lock.lock();
        playersWaiting.add(player);
        timeWaiting.add(1);
        lock.unlock();
    }

    public void run() {
        int time = 0;
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                 System.out.println("Error on Rank Lobby Thread: " + e.getMessage());
                 break;
            }

            if(show){
                if(!playersWaiting.isEmpty()){
                    System.out.println("Players waiting: " + playersWaiting.size() + " Time waiting: " + time++);
                } else {
                    time = 0;
                    continue;
                }
            }

            lock.lock();

            for (int i = 0; i < timeWaiting.size(); i++) {
                timeWaiting.set(i, timeWaiting.get(i) + 1);
            }


            List<Pair<Integer, Integer>> listOrdered = new ArrayList<>();

            for (int i = 0; i < playersWaiting.size(); i++) {
                int playersPoints = playersWaiting.get(i).getPoints();
                int maxPoints = playersPoints + timeWaiting.get(i) * step;
                int minPoints = playersPoints - timeWaiting.get(i) * step;
                listOrdered.add(new Pair<>(maxPoints, i));
                listOrdered.add(new Pair<>(minPoints, i));
            }

            listOrdered.sort((a, b) -> b.first - a.first);

            Set<Set<Integer>> playerSets = new HashSet<>();
            Set<Integer> currentOpen = new HashSet<>();
            for(Pair<Integer, Integer> pair : listOrdered) {
                if(currentOpen.contains(pair.second)) {
                    playerSets.add(new HashSet<>(currentOpen));
                    currentOpen.remove(pair.second);
                } else {
                    currentOpen.add(pair.second);
                }
            }

            for(Set<Integer> pSet : playerSets) {
                if(pSet.size() == numPlayers) {
                    List<Player> players = new ArrayList<>();
                    for(Integer i : pSet) {
                        players.add(playersWaiting.get(i));
                    }
                    Game game = new Game(players);
                    new Thread(game).start();

                    List<Integer> indicesToRemove = new ArrayList<Integer>(pSet);
                    Collections.sort(indicesToRemove, Collections.reverseOrder());
                    for(int i : indicesToRemove) {
                        playersWaiting.remove(i);
                        timeWaiting.remove(i);
                        for(Set<Integer> set : playerSets) {
                            if(!set.equals(pSet))
                                set.remove(i);
                        }
                    }
                }
            }
            
            if(show){
                if(!playersWaiting.isEmpty()){
                        System.out.println("List Ordered: " + listOrdered);
                        System.out.println("Player Sets: " + playerSets);
                        System.out.println("Players waiting: " + playersWaiting);
                    }
            }

            lock.unlock();  
        }
    }

    public void removePlayer(Player player) {
        lock.lock();
        int index = playersWaiting.indexOf(player);
        if(index != -1) {
            playersWaiting.remove(index);
            timeWaiting.remove(index);
        }
        lock.unlock();
    }
}
