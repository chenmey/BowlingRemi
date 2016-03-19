package com.meyouhas.bowlingremi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class SumSingleGameActivity extends AppCompatActivity {

    private DataBaseST db;
    private SumGameArrayAdapter sumGameArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_game);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();
        calculateResults(db.getCurrentGame());

        sumGameArrayAdapter = new SumGameArrayAdapter(this,0 ,db.getCurrentGame().getPlayersList(), db.getCurrentGame().getId());
        ListView listView = (ListView) findViewById(R.id.listView3);
        listView.setAdapter(sumGameArrayAdapter);
    }

    private void printResults() {
        for (Player p : db.getCurrentGamePlayersList()) {
            Log.d("Chenm:",p.getName() + ": profit - " + p.getGamesMap().get(db.getCurrentGameNum()).getProfit().toString() + " , games - " + p.getGamesMap().get(db.getCurrentGameNum()).getGames().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static public void calculateResults(Game currentGame) {

        // Sort player list according to last game score
        Player.setIsProfitCompare(false);
        Collections.sort(currentGame.getPlayersList());

        calculateProfit(currentGame);
        calculateGames(currentGame);

        Collections.reverse(currentGame.getPlayersList());
    }

    public static void calculateGames(Game currentGame) {
        ArrayList<Player> players = currentGame.getPlayersList();
        int currentGameNum = currentGame.getId();
        boolean evenNumOfPlayers = players.size() % 2 == 0;
        int medianIndex = players.size() / 2;
        PlayerGame medianPlayerGame = players.get(medianIndex).getGamesMap().get(currentGameNum);

        int sameMedianScore = 0;
        for (Player p : players) {
            if (p.getGamesMap().get(currentGameNum).getScore().equals(medianPlayerGame.getScore()))
                sameMedianScore++;
        }

        int gamesPayed = 0;
        boolean isFirstEqualScore = true;
        double gamesForEqualScores = 0;
        for (Player p : players) {
            PlayerGame playerGame = p.getGamesMap().get(currentGameNum);
            if (playerGame.getScore() > medianPlayerGame.getScore()) {
                playerGame.setGames(0.0);
            }
            else if (playerGame.getScore().equals(medianPlayerGame.getScore())) {
                if (isFirstEqualScore) {
                    gamesForEqualScores = (double)(players.size() - gamesPayed) / sameMedianScore;
                    isFirstEqualScore = false;
                }
                playerGame.setGames(gamesForEqualScores * (-1));
                gamesPayed += gamesForEqualScores;
                p.setTotalGames(p.getTotalGames() - gamesForEqualScores);

            }
            else {
                playerGame.setGames((double) -2);
                gamesPayed += 2;
                p.setTotalGames(p.getTotalGames() - (double)2);
            }
        }
    }

    private static void calculateProfit(Game currentGame) {
        int lastPlayerIndex = currentGame.getPlayersList().size() - 1;
        Integer highestScore = currentGame.getPlayersList().get(lastPlayerIndex).getGamesMap().get(currentGame.getId()).getScore();
        Integer tmpScore;
        Double tmpProfit;
        int sameScoreCounter = 0;
        Integer totalProfit = 0;
        for (Player p : currentGame.getPlayersList()) {
            PlayerGame playerGame = p.getGamesMap().get(currentGame.getId());
            tmpScore = playerGame.getScore();
            tmpProfit = 0.0;
            if (tmpScore.compareTo(highestScore) == 0) {
                sameScoreCounter++;
            }
            else if (highestScore >= 200 && tmpScore < 200) {
                tmpProfit = -10.0;
                totalProfit += 10;
            }
            else{
                tmpProfit = -5.0;
                totalProfit += 5;
            }

            playerGame.setProfit(tmpProfit);
            p.setTotalProfit(p.getTotalProfit() + tmpProfit);
        }

        Double profitPerWinnerPlayer = (double)totalProfit / sameScoreCounter;
        int tmpCounter = sameScoreCounter;
        int i = lastPlayerIndex;
        while (tmpCounter > 0) {
            Player p = currentGame.getPlayersList().get(i);
            p.getGamesMap().get(currentGame.getId()).setProfit(profitPerWinnerPlayer);
            p.setTotalProfit(p.getTotalProfit() + profitPerWinnerPlayer);
            i--;
            tmpCounter--;
        }
    }

    public void FinishGame(View view) {
        Intent intent = new Intent(this, ChooseActionActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
