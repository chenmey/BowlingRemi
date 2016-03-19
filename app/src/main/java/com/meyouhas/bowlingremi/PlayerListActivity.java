package com.meyouhas.bowlingremi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PlayerListActivity extends AppCompatActivity {

    private DataBaseST db;
    private ArrayAdapter<Player> playerArrayAdapter;
    private ListView listView;
    private boolean isCouplesGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        isCouplesGame = this.getIntent().getBooleanExtra("isCouplesGame",false);
        db = DataBaseST.getInstance();
        db.createNewGame(isCouplesGame);
     //   ArrayList playersList = db.getGamesList().get(db.getCurrentGameNum()).getPlayersList();
        playerArrayAdapter = new PlayerArrayAdapter(this,0 ,db.getCurrentGamePlayersList());
  //      playerArrayAdapter = new ArrayAdapter<Player>(this,android.R.layout.simple_list_item_1,db.getCurrentGamePlayersList());
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(playerArrayAdapter);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Quit if back is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            db.deleteCurrentGame();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                db.deleteCurrentGame();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addPlayerHandler(View view) {
        Log.d("mainActivity","addPlayerHandler");
        Toast toast;
        EditText et = (EditText) findViewById(R.id.editText);
        String newName = et.getText().toString();

        int res = db.addPlayer(newName);
        if (res == 1){
            toast = Toast.makeText(this, "Empty name!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (res == 2){
            toast = Toast.makeText(this, "Name is too long!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (res == 3){
            toast = Toast.makeText(this, "Name already exists!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        et.getText().clear();
        /*
        et.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); */
    }

    public void nextMainHandler(View view) {

        Game currentGame;
        Intent intent;

        if (!isCouplesGame) {
            if (db.getCurrentGamePlayersList().size() < 2) {
                Toast toast = Toast.makeText(this, "Singles game must have at least 2 players!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            intent = new Intent(this, SinglesScoresActivity.class);
        }
        else {
            if (db.getCurrentGamePlayersList().size() < 3) {
                Toast toast = Toast.makeText(this, "Couples game must have at least 3 players!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            intent = new Intent(this, CouplesScoresActivity.class);
        }

        generatePlayersNewGame();
        currentGame = db.getGamesList().get(db.getCurrentGameNum() - 1);
        currentGame.setPlayersList(db.getCurrentGamePlayersList());
        startActivity(intent);
    }

    private void generatePlayersNewGame(){

        for (Player player : db.getCurrentGamePlayersList()) {
            PlayerGame playerGame = new PlayerGame();
   //         player.getPlayerGames().add(playerGame);
            player.getGamesMap().put(db.getCurrentGameNum(), playerGame);
        }
    }

    public void cleanListHandler(View view) {
        db.cleanCurrentPlayersList();
  //      playerArrayAdapter.clear();
        playerArrayAdapter.notifyDataSetChanged();
    }


    class PlayerArrayAdapter extends ArrayAdapter<Player>{

        Context context;
        List<Player> objects;

        public PlayerArrayAdapter(Context context, int resource, List<Player> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Player player = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.players_list_view,null);

            TextView tv = (TextView) view.findViewById(R.id.playerName);
            tv.setText(player.getName());

            Button btn = (Button) view.findViewById(R.id.del_button);
            btn.setTag(position);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)v.getTag();
                    objects.remove(position);
                    playerArrayAdapter.notifyDataSetChanged();
                }
            });


            return view;
        }

    }
}
