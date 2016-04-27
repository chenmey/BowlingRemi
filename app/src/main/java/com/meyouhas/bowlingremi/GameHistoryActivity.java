package com.meyouhas.bowlingremi;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.headerlistview.HeaderListView;
import com.applidium.headerlistview.SectionAdapter;

import java.util.ArrayList;

public class GameHistoryActivity extends AppCompatActivity {

    private DataBaseST db;
    private SumGameArrayAdapter sumGameArrayAdapter;
    private SectionAdapter sectionAdapter = null;
    private HeaderListView headerListView;
    private ListView regularListView;
    private ArrayList<Game> gamesList;
    private Game currentGame;
    private boolean hasAtLeastOneChange;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();
        gamesList = db.getGamesList();
        hasAtLeastOneChange = false;
        isEditMode = false;

        regularListView = (ListView) findViewById(R.id.listViewHistory);
        headerListView = new HeaderListView(this);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.nameHistory);
        p.addRule(RelativeLayout.ABOVE, R.id.save_history);
        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        p.addRule(RelativeLayout.ALIGN_PARENT_END);
        headerListView.setLayoutParams(p);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.history_layout);
        rl.addView(headerListView);
        setContentView(rl);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> arrOfGames = new ArrayList<>();
        for (Integer i=1 ; i<= db.getCurrentGameNum() ; i++) {
            arrOfGames.add("Game " + i.toString());
        }
        ArrayAdapter<String> adp = new ArrayAdapter<> (this,android.R.layout.simple_spinner_dropdown_item,arrOfGames);
        spinner.setAdapter(adp);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3)
            {
                currentGame = gamesList.get(position);
                if (currentGame instanceof CouplesGame) {
                    setSumCouplesGameArrayAdapter();
                }
                else {
                    setSumGameArrayAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSumGameArrayAdapter () {

        regularListView.setVisibility(View.VISIBLE);
        headerListView.setVisibility(View.INVISIBLE);

        sumGameArrayAdapter = new SumGameArrayAdapter(this, 0, currentGame.getPlayersList(),currentGame.getId());
        regularListView.setAdapter(sumGameArrayAdapter);
        regularListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!sumGameArrayAdapter.isEdit())
                    return;

                Player player = sumGameArrayAdapter.getItem(position);

                ResultDialogFragment dialog = new ResultDialogFragment();

                Bundle b = new Bundle();
                b.putString("playerName", player.getName());
                b.putInt("playerIndex", position);
                b.putInt("invokedFrom", 2);

                dialog.setArguments(b);
                dialog.show(getFragmentManager(), "ResultFragment");
            }
        });
    }
    private void setSumCouplesGameArrayAdapter() {

        headerListView.setVisibility(View.VISIBLE);
        regularListView.setVisibility(View.INVISIBLE);

        if (sectionAdapter != null) {
            sectionAdapter.notifyDataSetChanged();
            return;
        }

        sectionAdapter = new SectionAdapter() {

            @Override
            public Object getRowItem(int section, int row) {
                return null;
            }

            @Override
            public int numberOfSections() {
                if (!(currentGame instanceof CouplesGame))
                    return  0;

                return ((CouplesGame)currentGame).getCouplesList().size();
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

                if (!(currentGame instanceof CouplesGame))
                    return  convertView;

                if (convertView == null) {
                    convertView =  getLayoutInflater().inflate(R.layout.sum_list_view, null);
                }

                TextView tvPlayerName = (TextView) convertView.findViewById(R.id.playerNameSum);
                TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.playerScoreCell);
                TextView tvPlayerGames = (TextView) convertView.findViewById(R.id.playerGamesPayed);
                TextView tvPlayerProfit = (TextView) convertView.findViewById(R.id.playerProfit);

                if (isEditMode) {
                    tvPlayerScore.setTextColor(Color.RED);
                    tvPlayerScore.setTypeface(null, Typeface.BOLD_ITALIC);
                }
                else {
                    tvPlayerScore.setTextColor(Color.BLACK);
                    tvPlayerScore.setTypeface(null, Typeface.NORMAL);
                }

                Couple couple = ((CouplesGame)currentGame).getCouplesList().get(section);
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

                if (!(currentGame instanceof CouplesGame))
                    return  convertView;

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.sum_list_view, null);

                TextView tvPlayerName = (TextView) convertView.findViewById(R.id.playerNameSum);
                TextView tvPlayerScore = (TextView) convertView.findViewById(R.id.playerScoreCell);
                TextView tvPlayerGames = (TextView) convertView.findViewById(R.id.playerGamesPayed);
                TextView tvPlayerProfit = (TextView) convertView.findViewById(R.id.playerProfit);

                Couple couple = ((CouplesGame)currentGame).getCouplesList().get(section);

                tvPlayerName.setText("Team " + couple.getTeamId());
                tvPlayerScore.setText(couple.getCombinedScore().toString());
                tvPlayerScore.setTextColor(Color.BLUE);
                tvPlayerScore.setTypeface(tvPlayerScore.getTypeface(), Typeface.BOLD_ITALIC);

                tvPlayerProfit.setText("");
                tvPlayerGames.setText("");

                convertView.setBackgroundColor(Color.LTGRAY);
                return convertView;
            }

            @Override
            public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
                super.onRowItemClick(parent, view, section, row, id);

                if (!isEditMode)
                    return;

                Couple couple = ((CouplesGame)currentGame).getCouplesList().get(section);
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
                b.putInt("invokedFrom", 4);

                dialog.setArguments(b);
                dialog.show(getFragmentManager(), "ResultFragment");

            }
        };

        headerListView.setAdapter(sectionAdapter);
    }

    public void EditScores(View view) {
        isEditMode = true;

        if (sumGameArrayAdapter != null)
            sumGameArrayAdapter.setIsEdit(true);

        Button b = (Button) findViewById(R.id.cancel_changes);
        b.setVisibility(View.VISIBLE);

        b = (Button) findViewById(R.id.save_history);
        b.setVisibility(View.VISIBLE);

        if (sectionAdapter != null)
            sectionAdapter.notifyDataSetChanged();

        if (sumGameArrayAdapter != null)
            sumGameArrayAdapter.notifyDataSetChanged();
    }

    public void savePlayerScore(int playerIndex, Integer score) {
        PlayerGame pg = sumGameArrayAdapter.getItem(playerIndex).getGamesMap().get(currentGame.getId());
        if (pg.getScore().equals(score)) {
            return;
        }

        pg.setScore(score);

        hasAtLeastOneChange = true;
        sumGameArrayAdapter.notifyDataSetChanged();
    }

    public void cancelChanges(View view) {
        for (Player p : currentGame.getPlayersList()) {
            PlayerGame pg = p.getGamesMap().get(currentGame.getId());
            pg.setScore(pg.getOrigScore());
        }
        
        changeToNonEditMode();
    }

    public void saveAllChanges(View view) {

        if(hasAtLeastOneChange) {
            // 1. decrease the games num to pay and profit from players totals
            // 2. update OrigScore
            for (Player p : currentGame.getPlayersList()) {
                PlayerGame pg = p.getGamesMap().get(currentGame.getId());
                p.setTotalGames(p.getTotalGames() - pg.getGames());
                p.setTotalProfit(p.getTotalProfit() - pg.getProfit());
                pg.setOrigScore(pg.getScore());
            }

            if (currentGame instanceof CouplesGame) {
                Integer playerOneScore;
                Integer playerTwoScore;
                for (Couple c : ((CouplesGame) currentGame).getCouplesList()) {
                    playerOneScore = c.getPlayerOne().getGamesMap().get(currentGame.getId()).getScore();
                    playerTwoScore = 190;
                    if (c.getPlayerTwo() != null)
                        playerTwoScore = c.getPlayerTwo().getGamesMap().get(currentGame.getId()).getScore();

                    c.setCombinedScore(playerOneScore + playerTwoScore);
                }
            }

            // Calculate and update once again according to the new changes
            Integer tmpGameId = db.getCurrentGameNum();
            db.setCurrentGameNum(currentGame.getId());
            if (currentGame instanceof CouplesGame)
                SumCoupleGameActivity.calculateResults((CouplesGame) currentGame);
            else
                SumSingleGameActivity.calculateResults(currentGame);

            db.setCurrentGameNum(tmpGameId);
        }


        changeToNonEditMode();

    }

    private void changeToNonEditMode(){
        isEditMode = false;

        if (sumGameArrayAdapter != null)
            sumGameArrayAdapter.setIsEdit(false);

        hasAtLeastOneChange = false;

        Button b = (Button) findViewById(R.id.cancel_changes);
        b.setVisibility(View.INVISIBLE);

        b = (Button) findViewById(R.id.save_history);
        b.setVisibility(View.GONE);


        if (sectionAdapter != null)
            sectionAdapter.notifyDataSetChanged();

        if (sumGameArrayAdapter != null)
            sumGameArrayAdapter.notifyDataSetChanged();
    }

    public void savePlayerScoreInCouple(int playerTeam, int playerIndex, Integer score) {

        PlayerGame pg;
        Couple couple = ((CouplesGame)currentGame).getCouplesList().get(playerTeam);
        if (playerIndex == 0)
            pg = couple.getPlayerOne().getGamesMap().get(currentGame.getId());
        else
            pg = couple.getPlayerTwo().getGamesMap().get(currentGame.getId());

        if (pg.getScore().equals(score)) {
            return;
        }

        pg.setScore(score);

        Integer playerOneScore = couple.getPlayerOne().getGamesMap().get(currentGame.getId()).getScore();
        Integer playerTwoScore = 190;
        if (couple.getPlayerTwo() != null)
            playerTwoScore = couple.getPlayerTwo().getGamesMap().get(currentGame.getId()).getScore();

        couple.setCombinedScore(playerOneScore + playerTwoScore);

        hasAtLeastOneChange = true;
        sectionAdapter.notifyDataSetChanged();

    }
}
