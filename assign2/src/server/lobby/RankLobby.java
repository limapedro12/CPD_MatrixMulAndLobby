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

            // List<Triplet<Integer, Integer, String>> listOrdered = new ArrayList<>();

            // for (int i = 0; i < playersWaiting.size(); i++) {
            //     int playersPoints = playersWaiting.get(i).getPoints();
            //     int maxPoints = playersPoints + timeWaiting.get(i) * step;
            //     int minPoints = playersPoints - timeWaiting.get(i) * step;
            //     listOrdered.add(new Triplet<>(maxPoints, i, "max"));
            //     listOrdered.add(new Triplet<>(minPoints, i, "min"));
            //     listOrdered.add(new Triplet<>(playersPoints, i, "center"));
            // }

            listOrdered.sort((a, b) -> b.first - a.first);

            // Set<Set<Integer>> playerSets = new HashSet<>();
            // Set<Integer> currentSet = new HashSet<>();
            // Set<Integer> tempSet = new HashSet<>();
            // for(Pair<Integer, Integer> pair : listOrdered) {
            //     if(tempSet.isEmpty()){
            //         if(!currentSet.isEmpty()){
            //             playerSets.add(new HashSet<>(currentSet));
            //             currentSet.clear();
            //         }
            //         tempSet.add(pair.second);
            //         currentSet.add(pair.second);
            //     } else {
            //         if(tempSet.contains(pair.second)) {
            //             tempSet.remove(pair.second);
            //         } else {
            //             tempSet.add(pair.second);
            //             currentSet.add(pair.second);
            //         }
            //     }
            // }

    //         // if(!currentSet.isEmpty()){
    //         //     playerSets.add(new HashSet<>(currentSet));
    //         // }

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

    //         // P:2000-R:300  P:1750-R:750  P:70-R:10  P:50-R:50  P:30-R:30 P:30-R:1  P:5-R:1
    //         // 
    //         // 2300-0-max 2000-0-center 1700-0-min 
    //         // 2500-1-max 1750-1-center 1000-1-min 
    //         // 80-2-max 70-2-center 60-2-min 
    //         // 100-3-max 50-3-center 0-3-min
    //         // 60-4-max 30-4-center 0-4-min
    //         // 31-5-max 30-5-center 29-5-min 
    //         // 6-6-max 5-6-center 4-6-min
    //         // 
    //         // 2500-1-max 2300-0-max 2000-0-center
    //         // 1750-1-center 1700-0-min 1000-1-min
    //         // 100-3-max 80-2-max 70-2-center
    //         // 60-4-max 60-2-min 50-3-center
    //         // 31-5-max 30-4-center 30-5-center
    //         // 29-5-min 6-6-max 5-6-center
    //         // 4-6-min 0-3-min 0-4-min
    //         // 
    //         // currOpen: 3, 4
    //         // {0, 1}, {1, 0}, {2}, {3, 2, 4, 5, 6}, {4, 3, 5, 6}, {5, 4}, {6}
    //         //
    //         // {0, 1}, {2}, {3,4}, {4,5}, {6}

            List<Set<Integer>> playerSets = new ArrayList<>();
            Set<Integer> currentOpen = new HashSet<>();

            for (int i = 0; i < playersWaiting.size(); i++) {
                playerSets.add(new HashSet<>({i}));
            }

            for(Triplet<Integer, Integer, String> triplet : listOrdered) {
                if(triplet.third.equals("center")) {
                    // for(int i : currentOpen) {
                    //     playerSets.get(i).add(triplet.second)
                    // }
                    playerSets.set(triplet.second, new HashSet<>(currentOpen));
                } else if(triplet.third.equals("max")) {
                    currentOpen.add(triplet.second);
                } else if(triplet.third.equals("min")) {
                    currentOpen.remove(triplet.second);
                }
            }

    //         for(Set<Integer> set : playerSets) {
    //             if(set.size() == numPlayers) {
    //                 Set<Player> players = new HashSet<>();
    //                 for(Integer i : set) {
    //                     players.add(playersWaiting.get(i));
    //                 }
    //                 new Thread(new Game(players)).start();
    //                 for(int i : set) {
    //                     playersWaiting.remove(i);
    //                     timeWaiting.remove(i);
    //                 }
    //             }
    //         } 

    //         if(show){
    //             if(!playersWaiting.isEmpty()){
    //                 System.out.println("List Ordered: " + listOrdered);
    //                 System.out.println("Player Sets: " + playerSets);
    //             }
    //         }

    //         lock.unlock();  
    //     }
    // }

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
