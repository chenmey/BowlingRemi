package com.meyouhas.bowlingremi;

import java.util.ArrayList;

/**
 * Created by Meyouhas on 15-Feb-16.
 */
public class Game {

    private Integer id;
    private ArrayList<Player> playersList;

    public Game(Integer id, ArrayList<Player> playersList) {
        this.id = id;

        if (playersList == null)
            this.playersList = null;
        else
            this.playersList = new ArrayList<>(playersList);
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        this.playersList = new ArrayList<>(playersList);
    }

    public ArrayList<Player> getPlayersList() {
        return playersList;
    }
    public Integer getId() {
        return id;
    }
}
