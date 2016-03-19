package com.meyouhas.bowlingremi;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applidium.headerlistview.HeaderListView;
import com.applidium.headerlistview.SectionAdapter;

import java.util.Collections;

public class SumCoupleGameActivity extends AppCompatActivity {

    private DataBaseST db;
    private CouplesGame currentGame;
    private SectionAdapter sectionAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        sectionAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_couple_game);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();
        currentGame = (CouplesGame) db.getGamesList().get(db.getCurrentGameNum() - 1);

        calculateResults();

        HeaderListView list = new HeaderListView(this);
        sectionAdapter = new SectionAdapter() {

            @Override
            public Object getRowItem(int section, int row) {
                return null;
            }

            @Override
            public int numberOfSections() {
                return currentGame.getCouplesList().size();
            }

            @Override
            public int numberOfRows(int section) {
                return 2;
            }

            @Override
            public boolean hasSectionHeaderView(int section) {
                return true;
            }

            @Override
            public View getRowView(int section, int row, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView =  getLayoutInflater().inflate(R.layout.sum_list_view, null);
                }

                TextView tvPlayerName = (TextView) convertView.findViewById(R.id.playerNameSum);
                TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.playerScoreCell);
                TextView tvPlayerGames = (TextView) convertView.findViewById(R.id.playerGamesPayed);
                TextView tvPlayerProfit = (TextView) convertView.findViewById(R.id.playerProfit);

                Couple couple = currentGame.getCouplesList().get(section);
                if (row == 0) {
                    PlayerGame pg = couple.getPlayerOne().getGamesMap().get(couple.getGameId());
                    tvPlayerName.setText(couple.getPlayerOne().getName());
                    tvPlayerScore.setText(pg.getScore().toString());
                    tvPlayerGames.setText(pg.getGames().toString());
                    tvPlayerProfit.setText(pg.getProfit().toString());
                }
                else {
                    if (couple.getPlayerTwo() != null) {
                        PlayerGame pg = couple.getPlayerTwo().getGamesMap().get(couple.getGameId());
                        tvPlayerName.setText(couple.getPlayerTwo().getName());
                        tvPlayerScore.setText(pg.getScore().toString());
                        tvPlayerGames.setText(pg.getGames().toString());
                        tvPlayerProfit.setText(pg.getProfit().toString());
                    }
                    else {
                        tvPlayerName.setText("Blind");
                        tvPlayerScore.setText("190");
                        tvPlayerProfit.setText("");
                        tvPlayerGames.setText("");
                    }
                }

                return convertView;
            }

            @Override
            public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.sum_list_view, null);

                TextView tvPlayerName = (TextView) convertView.findViewById(R.id.playerNameSum);
                TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.playerScoreCell);
                TextView tvPlayerGames = (TextView) convertView.findViewById(R.id.playerGamesPayed);
                TextView tvPlayerProfit = (TextView) convertView.findViewById(R.id.playerProfit);

                Couple couple = currentGame.getCouplesList().get(section);

                tvPlayerName.setText("Team " + couple.getTeamId());
                tvPlayerScore.setText(currentGame.getCouplesList().get(section).getCombinedScore().toString());
                tvPlayerScore.setTextColor(Color.BLUE);
                tvPlayerScore.setTypeface(tvPlayerScore.getTypeface(), Typeface.BOLD_ITALIC);

                tvPlayerProfit.setText("");
                tvPlayerGames.setText("");

                convertView.setBackgroundColor(Color.LTGRAY);
                return convertView;
            }
        };

        list.setAdapter(sectionAdapter);

        // using an existing listviewsumcouples attributes - not good enough.
        RelativeLayout.LayoutParams relativeLayoutParam = (RelativeLayout.LayoutParams) findViewById(R.id.listViewSumCouples).getLayoutParams();
        list.setLayoutParams(relativeLayoutParam);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.scores_couples_layout);
        rl.addView(list);
        setContentView(rl);
    }

    private void calculateResults() {

        Collections.sort(currentGame.getCouplesList());
        Collections.sort(currentGame.getPlayersList());

        SumSingleGameActivity.calculateGames(currentGame);
        calculateProfit(currentGame);

        Collections.reverse(currentGame.getCouplesList());
        Collections.reverse(currentGame.getPlayersList());
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

    private static void calculateProfit(CouplesGame currentGame) {
        int lastCoupleIndex = currentGame.getCouplesList().size() - 1;
        Integer highestScore = currentGame.getCouplesList().get(lastCoupleIndex).getCombinedScore();
        Integer tmpScore;
        Double tmpProfit;
        int sameScoreCounter = 0;
        Integer totalProfit = 0;

        for (Couple c : currentGame.getCouplesList()) {
            tmpScore = c.getCombinedScore();
            tmpProfit = 0.0;
            if (tmpScore.compareTo(highestScore) == 0) {
                sameScoreCounter++;
            }
            else if (highestScore >= 400 && tmpScore < 400) {
                tmpProfit = -20.0;
                totalProfit += 20;
            }
            else{
                tmpProfit = -10.0;
                totalProfit += 10;
            }

            c.setCombinedProfit(tmpProfit);

            if (c.getPlayerTwo() == null) {
                c.getPlayerOne().getGamesMap().get(c.getGameId()).setProfit(tmpProfit);
                c.getPlayerOne().setTotalProfit(c.getPlayerOne().getTotalProfit() + tmpProfit);

            }
            else {
                c.getPlayerOne().getGamesMap().get(c.getGameId()).setProfit(tmpProfit / 2.0);
                c.getPlayerOne().setTotalProfit(c.getPlayerOne().getTotalProfit() + (tmpProfit / 2.0));

                c.getPlayerTwo().getGamesMap().get(c.getGameId()).setProfit(tmpProfit / 2.0);
                c.getPlayerTwo().setTotalProfit(c.getPlayerTwo().getTotalProfit() + (tmpProfit / 2.0));
            }
        }

        Double profitPerWinnerPlayer = (double)totalProfit / sameScoreCounter;
        int tmpCounter = sameScoreCounter;
        int i = lastCoupleIndex;
        while (tmpCounter > 0) {
            Couple c = currentGame.getCouplesList().get(i);
            c.setCombinedProfit(profitPerWinnerPlayer);

            if (c.getPlayerTwo() == null) {
                c.getPlayerOne().getGamesMap().get(c.getGameId()).setProfit(profitPerWinnerPlayer);
                c.getPlayerOne().setTotalProfit(c.getPlayerOne().getTotalProfit() + profitPerWinnerPlayer);

            }
            else {
                c.getPlayerOne().getGamesMap().get(c.getGameId()).setProfit(profitPerWinnerPlayer / 2.0);
                c.getPlayerOne().setTotalProfit(c.getPlayerOne().getTotalProfit() + (profitPerWinnerPlayer / 2.0));

                c.getPlayerTwo().getGamesMap().get(c.getGameId()).setProfit(profitPerWinnerPlayer / 2.0);
                c.getPlayerTwo().setTotalProfit(c.getPlayerTwo().getTotalProfit() + (profitPerWinnerPlayer / 2.0));
            }

            i--;
            tmpCounter--;
        }
    }

}
