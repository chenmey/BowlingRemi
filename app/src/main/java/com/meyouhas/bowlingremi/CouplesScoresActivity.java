package com.meyouhas.bowlingremi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.headerlistview.HeaderListView;
import com.applidium.headerlistview.SectionAdapter;

import java.util.ArrayList;

public class CouplesScoresActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_couples_scores);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();
        currentGame = (CouplesGame) db.getGamesList().get(db.getCurrentGameNum() - 1);

        // TODO: 18-Mar-16 adding current Couples List in DB (to avoid changes in order)
        // TODO: 08-Mar-16 generating Couples - need to think if refresh is allowed
        generateCouples();

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
                    convertView =  getLayoutInflater().inflate(R.layout.scores_list_view, null);
                }

                TextView tvPlayerName = (TextView) convertView.findViewById(R.id.playerName);
                TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.playerScore);

                Couple couple = currentGame.getCouplesList().get(section);
                if (row == 0) {
                    tvPlayerName.setText(couple.getPlayerOne().getName());
                    Integer playerScore = couple.getPlayerOne().getGamesMap().get(couple.getGameId()).getScore();
                    if (playerScore == -1)
                        tvPlayerScore.setText("?");
                    else
                        tvPlayerScore.setText(playerScore.toString());
                }
                else {
                    if (couple.getPlayerTwo() != null) {
                        tvPlayerName.setText(couple.getPlayerTwo().getName());
                        Integer playerScore = couple.getPlayerTwo().getGamesMap().get(couple.getGameId()).getScore();
                        if (playerScore == -1)
                            tvPlayerScore.setText("?");
                        else
                            tvPlayerScore.setText(playerScore.toString());
                    }
                    else {
                        tvPlayerName.setText("Blind");
                        tvPlayerScore.setText("190");
                    }
                }


                return convertView;
            }

            @Override
            public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
                super.onRowItemClick(parent, view, section, row, id);

                // TODO: 16-Mar-16 change couples to have arraylist of players
                Couple couple = currentGame.getCouplesList().get(section);
                Player player;
                if (row == 0)
                    player = couple.getPlayerOne();
                else {
                    if (couple.getPlayerTwo() == null)
                        return;

                    player = couple.getPlayerTwo();
                }

                ResultDialogFragment dialog = new ResultDialogFragment();
                Bundle b = new Bundle();
                b.putString("playerName", player.getName());
                b.putInt("playerTeam", section);
                b.putInt("playerIndex", row);
                // TODO: 16-Mar-16 adding Macro or Enum
                b.putInt("invokedFrom", 3);

                dialog.setArguments(b);
                dialog.show(getFragmentManager(), "ResultFragment");

            }

            @Override
            public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = (TextView) getLayoutInflater().inflate(getResources().getLayout(android.R.layout.simple_list_item_1), null);

                Couple couple = currentGame.getCouplesList().get(section);
                ((TextView) convertView).setText("Team " + couple.getTeamId());
                convertView.setBackgroundColor(Color.LTGRAY);
                return convertView;
            }
        };
        list.setAdapter(sectionAdapter);

        // using an existing listview2 attributes - not good enough.
        RelativeLayout.LayoutParams relativeLayoutParam = (RelativeLayout.LayoutParams) findViewById(R.id.listView2).getLayoutParams();
        list.setLayoutParams(relativeLayoutParam);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.scores_layout);
        rl.addView(list);
        setContentView(rl);
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

    private void generateCouples() {
        ArrayList<Player> tmpPlayersList = new ArrayList<>(currentGame.getPlayersList());
        ArrayList<Couple> couplesList = new ArrayList<>();
        int randomIndex;
        Integer teamId;
        Couple newCouple;
        Player playerOne = null;
        Player playerTwo = null;

        teamId = 0;
        while (tmpPlayersList.size() > 0){
            playerOne = null;
            playerTwo = null;

            teamId++;

            randomIndex = (int) (tmpPlayersList.size() * Math.random());
            playerOne = tmpPlayersList.get(randomIndex);
            tmpPlayersList.remove(randomIndex);

            // check if there is at least one player left for second player team
            if (tmpPlayersList.size() > 0) {
                randomIndex = (int) (tmpPlayersList.size() * Math.random());
                playerTwo = tmpPlayersList.get(randomIndex);
                tmpPlayersList.remove(randomIndex);
            }

            newCouple = new Couple(playerOne, playerTwo, currentGame.getId(), teamId);
            couplesList.add(newCouple);
        }

        if (playerTwo == null) {
            Couple tmpCouple = couplesList.get(couplesList.size() - 1);
            tmpCouple.setCombinedScore(190);
        }

        currentGame.setCouplesList(couplesList);

    }


    
    public void NextResults(View view) {

        if (ValidatePlayersScores()) {
            Intent intent = new Intent(this, SumCoupleGameActivity.class);
            startActivity(intent);
        }
        else {
            Toast toast = Toast.makeText(this, "Some of the players have no score", Toast.LENGTH_SHORT);
            toast.show();
        }
        for (Couple c : currentGame.getCouplesList()) {
            Integer playerOneScore = c.getPlayerOne().getGamesMap().get(c.getGameId()).getScore();
            if (c.getPlayerTwo() == null)
                c.setCombinedScore(190 + playerOneScore);
            else {
                Integer playerTwoScore = c.getPlayerTwo().getGamesMap().get(c.getGameId()).getScore();
                c.setCombinedScore(playerOneScore + playerTwoScore);
            }
        }
    }

    private boolean ValidatePlayersScores() {
        for (Couple couple : currentGame.getCouplesList()) {
            if (couple.getPlayerOne().getGamesMap().get(couple.getGameId()).getScore() == -1)
                return false;
            if (couple.getPlayerTwo() != null &&
                    couple.getPlayerTwo().getGamesMap().get(couple.getGameId()).getScore() == -1)
                return false;
        }

        return true;
    }

    public void updatePlayerScoreInCouple(int playerTeam, int playerIndex, Integer score) {
        Couple couple = currentGame.getCouplesList().get(playerTeam);
        Player player;
        if (playerIndex == 0)
            player = couple.getPlayerOne();
        else
            player = couple.getPlayerTwo();

        player.getGamesMap().get(couple.getGameId()).setScore(score);
        couple.setCombinedScore(couple.getCombinedScore() + score);
        sectionAdapter.notifyDataSetChanged();
    }
}
