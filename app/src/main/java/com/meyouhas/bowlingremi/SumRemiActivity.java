package com.meyouhas.bowlingremi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.Collections;

public class SumRemiActivity extends AppCompatActivity {

    private DataBaseST db;
    private SumRemiArrayAdapter sumRemiArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_remi);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = DataBaseST.getInstance();

        db.cleanPlayersWithNoGames();
        Player.setIsProfitCompare(true);
        Collections.sort(db.getAllPlayersList());
        Collections.reverse(db.getAllPlayersList());

        sumRemiArrayAdapter = new SumRemiArrayAdapter(this,0 ,db.getAllPlayersList());
        ListView listView = (ListView) findViewById(R.id.listViewSumRemi);
        listView.setAdapter(sumRemiArrayAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Player.setIsProfitCompare(true);
        Collections.sort(db.getAllPlayersList());
        Collections.reverse(db.getAllPlayersList());
        sumRemiArrayAdapter.notifyDataSetChanged();
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

    public void FinishRemi(View view) {
        // TODO: 14-Jan-16 save all game in remote DB
        Intent intent = new Intent(this, StartRemiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void ShowHistory(View view) {
        Intent intent = new Intent(this, GameHistoryActivity.class);
        startActivity(intent);
    }
}
