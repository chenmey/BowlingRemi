package com.meyouhas.bowlingremi;

/**
 * Created by Meyouhas on 06-Mar-16.
 */
public class Couple implements Comparable<Couple> {

    private Integer gameId;
    private Integer teamId;
    private Player playerOne;
    private Player playerTwo;
    private Integer combinedScore;
    private Double combinedProfit;

    public Couple(Player playerOne, Player playerTwo, Integer gameId, Integer teamId) {

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameId = gameId;
        this.teamId = teamId;
        this.combinedScore = 0;
        this.combinedProfit = 0.0;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getCombinedScore() {
        return combinedScore;
    }

    public void setCombinedScore(Integer combinedScore) {
        this.combinedScore = combinedScore;
    }

    public Double getCombinedProfit() {
        return combinedProfit;
    }

    public void setCombinedProfit(Double combinedProfit) {
        this.combinedProfit = combinedProfit;
    }

    @Override
    public int compareTo(Couple another) {
        return (this.combinedScore - another.combinedScore);
    }
}
