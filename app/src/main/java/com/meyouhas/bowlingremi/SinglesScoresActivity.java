package com.meyouhas.bowlingremi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class SinglesScoresActivity extends AppCompatActivity {

    private DataBaseST db;
    private SinglesScoresArrayAdapter singlesScoresArrayAdapter;
    private Game currentGame;

    @Override
    protected void onResume() {
        super.onResume();
        singlesScoresArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singles_scores);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();
        currentGame = db.getGamesList().get(db.getCurrentGameNum() - 1);

        singlesScoresArrayAdapter = new SinglesScoresArrayAdapter(this,0 ,currentGame.getPlayersList());
        ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(singlesScoresArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player player = currentGame.getPlayersList().get(position);
                //       Toast toast = Toast.makeText(view.getContext(),player.getName(), Toast.LENGTH_SHORT);
                //        toast.show();
                ResultDialogFragment dialog = new ResultDialogFragment();

                Bundle b = new Bundle();
                b.putString("playerName", player.getName());
                b.putInt("playerIndex", position);
                b.putInt("invokedFrom", 1);

                dialog.setArguments(b);
                dialog.show(getFragmentManager(), "ResultFragment");
                // TODO: 17-Jan-16 memory leak upon InitDB?
            }
        });
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

    public void updatePlayerScore(int index, Integer score){
   //     ArrayList<PlayerGame> playerGames = db.getCurrentGamePlayersList().get(index).getPlayerGames();
        HashMap<Integer,PlayerGame> playerGames = currentGame.getPlayersList().get(index).getGamesMap();
        PlayerGame playerGame = playerGames.get(currentGame.getId());
        playerGame.setScore(score);
        playerGame.setOrigScore(score);
        singlesScoresArrayAdapter.notifyDataSetChanged();
    }

    public void NextResults(View view) {
        if (ValidatePlayersScores()) {
            Intent intent = new Intent(this, SumSingleGameActivity.class);
            startActivity(intent);
        }
        else {
            Toast toast = Toast.makeText(this,"Some of the players have no score", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean ValidatePlayersScores() {
        for (Player player : currentGame.getPlayersList()) {
            if(player.getGamesMap().get(currentGame.getId()).getScore() == -1)
                return false;
        }
        return true;
    }

    class SinglesScoresArrayAdapter extends ArrayAdapter<Player>{

        Context context;
        List<Player> objects;

        public SinglesScoresArrayAdapter(Context context, int resource, List<Player> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Player player = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.scores_list_view,null);

            TextView tv = (TextView) view.findViewById(R.id.playerName);
            tv.setText(player.getName());

            tv = (TextView) view.findViewById(R.id.playerScore);
    //        Integer playerScore = player.getPlayerGames().get(db.getCurrentGameNum()-1).getScore();
            Integer playerScore = player.getGamesMap().get(currentGame.getId()).getScore(); //CRASHHHH
            if (playerScore == -1)
                tv.setText("?");
            else
                tv.setText(playerScore.toString());

            return view;
        }

    }

}
