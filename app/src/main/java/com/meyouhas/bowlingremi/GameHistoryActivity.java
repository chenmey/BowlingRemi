package com.meyouhas.bowlingremi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class GameHistoryActivity extends AppCompatActivity {

    private DataBaseST db;
    private SumGameArrayAdapter sumGameArrayAdapter;
    private ArrayList<Game> gamesList;
    private Game currentGame;
    private boolean hasAtLeastOneChange;

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

        // Set new arrayadapter to hold the first game list.
        setSumGameArrayAdapter(0);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> arrOfGames = new ArrayList<String>();
        for (Integer i=1 ; i<= db.getCurrentGameNum() ; i++) {
            arrOfGames.add("Game " + i.toString());
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,arrOfGames);
        spinner.setAdapter(adp);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3)
            {
                setSumGameArrayAdapter(position);
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

    private void setSumGameArrayAdapter (Integer gamePosition) {
        currentGame = gamesList.get(gamePosition);
        if (currentGame instanceof Game)
            sumGameArrayAdapter = new SumGameArrayAdapter(this, 0, ((Game) currentGame).getPlayersList(),currentGame.getId());
        ListView listView = (ListView) findViewById(R.id.listViewHistory);
        listView.setAdapter(sumGameArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (sumGameArrayAdapter.isEdit() == false)
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

    public void EditScores(View view) {
        sumGameArrayAdapter.setIsEdit(true);
        Button b = (Button) findViewById(R.id.cancel_changes);
        b.setVisibility(View.VISIBLE);

        b = (Button) findViewById(R.id.save_history);
        b.setVisibility(View.VISIBLE);
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
/*
        Toast toast = Toast.makeText(this, "TESTTTTTTTTTTT", Toast.LENGTH_SHORT);
        toast.show(); */
    }

    public void cancelChanges(View view) {
        if (currentGame instanceof Game) {
            for (Player p : ((Game) currentGame).getPlayersList()) {
                PlayerGame pg = p.getGamesMap().get(currentGame.getId());
                pg.setScore(pg.getOrigScore());
            }
        }
        
        changeToNonEditMode();

    }

    public void saveAllChanges(View view) {

        if(hasAtLeastOneChange) {
            // 1. decrease the games num to pay and profit from players totals
            // 2. update OrigScore
            if (currentGame instanceof Game) {
                for (Player p : ((Game) currentGame).getPlayersList()) {
                    PlayerGame pg = p.getGamesMap().get(currentGame.getId());
                    p.setTotalGames(p.getTotalGames() - pg.getGames());
                    p.setTotalProfit(p.getTotalProfit() - pg.getProfit());
                    pg.setOrigScore(pg.getScore());
                }
            }

            // Calculate and update once again according to the new changes
            Integer tmpGameId = db.getCurrentGameNum();
            db.setCurrentGameNum(currentGame.getId());
            if (currentGame instanceof Game)
                SumSingleGameActivity.calculateResults((Game) currentGame);
            db.setCurrentGameNum(tmpGameId);
        }


        changeToNonEditMode();

    }

    private void changeToNonEditMode(){
        sumGameArrayAdapter.setIsEdit(false);
        hasAtLeastOneChange = false;

        Button b = (Button) findViewById(R.id.cancel_changes);
        b.setVisibility(View.INVISIBLE);

        b = (Button) findViewById(R.id.save_history);
        b.setVisibility(View.GONE);
        sumGameArrayAdapter.notifyDataSetChanged();
    }
}
