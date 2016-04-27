package com.meyouhas.bowlingremi;

import java.util.ArrayList;


/**
 * Created by Meyouhas on 06-Nov-15.
 */
public class DataBaseST {
    private static DataBaseST ourInstance = new DataBaseST();
    private Integer currentGameNum;
    private ArrayList<Player> currentGamePlayersList;
    private ArrayList<Couple> currentGameCouplesList;
    private ArrayList<Player> allPlayersList;
    private ArrayList<Game> gamesList;
    private Game currentGame;


    public void InitDB() {
        allPlayersList = new ArrayList<>();
        currentGamePlayersList = new ArrayList<>();
        gamesList = new ArrayList<>();
        currentGameCouplesList = new ArrayList<>();
        currentGameNum = 0;
    }

    public static DataBaseST getInstance() {
        return ourInstance;
    }

    public ArrayList<Player> getCurrentGamePlayersList() {
        return currentGamePlayersList;
    }
    public ArrayList<Player> getAllPlayersList() {
        return allPlayersList;
    }

    public ArrayList<Game> getGamesList() {
        return gamesList;
    }

    public void createNewGame(boolean isCouplesGame) {
        Game game;

        currentGameNum++;
        if (isCouplesGame)
            game = new CouplesGame(currentGameNum, null,null);
        else
            game = new Game(currentGameNum,null);

        gamesList.add(game);
    }
    public void deleteCurrentGame() {
        gamesList.remove(gamesList.size() - 1);
        currentGameNum--;
    }

    public void cleanCurrentPlayersList() {
       currentGamePlayersList.clear();
    }

    public boolean playerExistsInCurrentList(String newName){
        for (Player player: currentGamePlayersList) {
           if(player.getName().equals(newName))
               return true;
        }
        return false;
    }

    public int addPlayer(String newName) {

        if(newName.isEmpty()) {
            return 1;
        }

        if(newName.length() > 10) {
            return 2;
        }

        if(playerExistsInCurrentList(newName)) {
            return 3;
        }

        Player player = PlayerExistInAllPlayersList(newName);
        if (player == null) {
            player = new Player(newName);
            allPlayersList.add(player);
        }
        currentGamePlayersList.add(player);
        return 0;
    }

    private Player PlayerExistInAllPlayersList(String newName) {
        for (Player player: allPlayersList) {
            if(player.getName().equals(newName))
                return player;
        }
        return null;
    }

    public Game getCurrentGame() {
        return this.getGamesList().get(this.getCurrentGameNum() - 1);
    }
    public Integer getCurrentGameNum() {
        return currentGameNum;
    }
    public void setCurrentGameNum(Integer currentGameNum) {
        this.currentGameNum = currentGameNum;
    }

    private DataBaseST() {}

    public ArrayList<Couple> getCurrentGameCouplesList() {
        return currentGameCouplesList;
    }

    public void setCurrentGameCouplesList(ArrayList<Couple> currentGameCouplesList) {
        this.currentGameCouplesList = currentGameCouplesList;
    }

    public void cleanPlayersWithNoGames() {
        ArrayList<Player> tmp = new ArrayList<>(getAllPlayersList());
        for (Player p : tmp) {
            if (p.getGamesMap().size() == 0)
                getAllPlayersList().remove(p);
        }
    }
}