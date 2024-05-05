package server.lobby;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
                if(tempSet.isEmpty()){
                    
                } else {
                    if(currentOpen.contains(pair.second)) {
                        playerSets.add(new HashSet<>(currentOpen));
                        currentOpen.remove(pair.second);
                    } else {
                        currentOpen.add(pair.second);
                    }
                }
            }

            List<Set<Integer>> playerSets = new ArrayList<>();
            Set<Integer> currentOpen = new HashSet<>();

            for (int i = 0; i < playersWaiting.size(); i++) {
                playerSets.add(new HashSet<>({i}));
            }

            for(Triplet<Integer, Integer, String> triplet : listOrdered) {
                if(triplet.third.equals("center")) {
                    playerSets.set(triplet.second, new HashSet<>(currentOpen));
                } else if(triplet.third.equals("max")) {
                    currentOpen.add(triplet.second);
                } else if(triplet.third.equals("min")) {
                    currentOpen.remove(triplet.second);
                }
            }

            for(Set<Integer> set : playerSets) {
                if(set.size() == numPlayers) {
                    List<Player> players = new ArrayList<>();
                    for(Integer i : set) {
                        players.add(playersWaiting.get(i));
                    }
                    Game game = new Game(players);
                    game.start();
                    for(Integer i : set) {
                        playersWaiting.remove(i);
                        timeWaiting.remove(i);
                        for(Set<Integer> set : playerSets) {
                            set.remove(i);
                        }
                    }
                }
            }
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
