package com.meyouhas.bowlingremi;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Meyouhas on 06-Nov-15.
 */
public class Player implements Comparable<Player> {

    private String name;
    private Integer id;
    private Double totalProfit;
    private Double totalGames;
    private HashMap<Integer,PlayerGame> gamesMap;
    private ArrayList<PlayerGame> playerGames;
    private static Integer nextID = 0;
    private static DataBaseST db = DataBaseST.getInstance();
    private static boolean isProfitCompare = false;

    public static void setIsProfitCompare(boolean isProfitCompare) {
        Player.isProfitCompare = isProfitCompare;
    }

    public static Integer generateID() {
        return nextID++;
    }

    public Player(String name) {
        this.name = name;
        this.totalProfit = 0.0;
        this.totalGames = 0.0;
        this.id = generateID();
        this.playerGames = new ArrayList<PlayerGame>();
        this.gamesMap = new HashMap<Integer,PlayerGame>();
    }

    public ArrayList<PlayerGame> getPlayerGames() {
        return playerGames;
    }
    public HashMap<Integer,PlayerGame> getGamesMap() {
        return gamesMap;
    }
    public String getName() {
        return name;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Double getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(Double totalGames) {
        this.totalGames = totalGames;
    }

    public static Integer getNextID() {
        return nextID;
    }

    public static void setNextID(Integer nextID) {
        Player.nextID = nextID;
    }

    public void setName(String name) {
        this.name = name;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return this.name;
    }

 //   public int compareTo(Player p){

   // }

    @Override
    public int compareTo(Player p) {
        Integer thisPlayerLastGameScore = 0;
        Integer givenPlayerLastGameScore = 0;
        if (isProfitCompare) {
            thisPlayerLastGameScore = getTotalProfit().intValue();
            givenPlayerLastGameScore = p.getTotalProfit().intValue();
        }
        else {
            thisPlayerLastGameScore = gamesMap.get(db.getCurrentGameNum()).getScore();
            givenPlayerLastGameScore = p.gamesMap.get(db.getCurrentGameNum()).getScore();
        }

        return (thisPlayerLastGameScore - givenPlayerLastGameScore);
    }
}
