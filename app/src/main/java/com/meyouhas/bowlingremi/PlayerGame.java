package com.meyouhas.bowlingremi;

/**
 * Created by Meyouhas on 06-Nov-15.
 */
public class PlayerGame {

    private Integer score;
    private Integer origScore; //Remember the last score after setting the new score and before save
    private Double profit;
    private Double gamesEarned;

    public PlayerGame() {
        this.score = -1;
        this.origScore = -1;
        this.profit = (double) 0;
        this.gamesEarned = (double) 0;
    }


    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getGames() {
        return gamesEarned;
    }

    public void setGames(Double games) {
        this.gamesEarned = games;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getOrigScore() {
        return origScore;
    }

    public void setOrigScore(Integer origScore) {
        this.origScore = origScore;
    }

}
