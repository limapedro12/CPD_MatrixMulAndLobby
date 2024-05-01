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

            // P:2000-R:300  P:1000-R:750  P:70-R:10  P:50-R:50  P:10-R:1  P:5-R:1
            // 
            // 2300-0 1700-0 1750-1 350-1 80-2 60-2 100-3 0-3 11-4 9-4 6-5 4-5
            // 
            // 2300-0 1750-1 1700-0 350-1 100-3 80-2 60-2 11-4 9-4 6-5 4-5 0-3
            // |------------------------| |----------------------------------|
            //
            // {0, 1}, {2, 3, 4, 5}

            List<Pair<Integer, Integer>> listOrdered = new ArrayList<>();

            for (int i = 0; i < playersWaiting.size(); i++) {
                int playersPoints = playersWaiting.get(i).getPoints();
                int maxPoints = playersPoints + timeWaiting.get(i) * step;
                int minPoints = playersPoints - timeWaiting.get(i) * step;
                listOrdered.add(new Pair<>(maxPoints, i));
                listOrdered.add(new Pair<>(minPoints, i));
            }

            listOrdered.sort((a, b) -> a.first - b.first);

            Set<Set<Integer>> playerSets = new HashSet<>();
            Set<Integer> currentSet = new HashSet<>();
            Set<Integer> tempSet = new HashSet<>();
            for(Pair<Integer, Integer> pair : listOrdered) {
                if(tempSet.isEmpty()){
                    if(!currentSet.isEmpty()){
                        playerSets.add(new HashSet<>(currentSet));
                        currentSet.clear();
                    }
                    tempSet.add(pair.second);
                    currentSet.add(pair.second);
                } else {
                    if(tempSet.contains(pair.second)) {
                        tempSet.remove(pair.second);
                    } else {
                        tempSet.add(pair.second);
                        currentSet.add(pair.second);
                    }
                }
            }

            if(!currentSet.isEmpty()){
                playerSets.add(new HashSet<>(currentSet));
            }

            for(Set<Integer> set : playerSets) {
                if(set.size() == numPlayers) {
                    Set<Player> players = new HashSet<>();
                    for(Integer i : set) {
                        players.add(playersWaiting.get(i));
                    }
                    new Thread(new Game(players)).start();
                    for(int i : set) {
                        playersWaiting.remove(i);
                        timeWaiting.remove(i);
                    }
                }
            } 

            if(show){
                if(!playersWaiting.isEmpty()){
                    System.out.println("List Ordered: " + listOrdered);
                    System.out.println("Player Sets: " + playerSets);
                }
            }

            lock.unlock();  
        }
    }
}
