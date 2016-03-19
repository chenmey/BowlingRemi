package com.meyouhas.bowlingremi;

import java.util.ArrayList;

/**
 * Created by Meyouhas on 07-Mar-16.
 */

public class CouplesGame extends Game {

    private ArrayList<Couple> couplesList;

    public CouplesGame(Integer id, ArrayList<Player> playersList, ArrayList<Couple> couplesList) {
        super(id, playersList);

        if (couplesList == null)
            this.couplesList = null;
        else
            this.couplesList = new ArrayList<>(couplesList);
    }

    public ArrayList<Couple> getCouplesList() {
        return couplesList;
    }

    public void setCouplesList(ArrayList<Couple> couplesList) {
        this.couplesList = new ArrayList<>(couplesList);
    }

}
