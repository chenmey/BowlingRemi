package com.meyouhas.bowlingremi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartRemiActivity extends AppCompatActivity {

    private DataBaseST db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_remi);
        db = DataBaseST.getInstance();
    }

    public void CreateNewRemiGame(View view) {
        db.InitDB();
        Intent intent = new Intent(this, ChooseActionActivity.class);
        startActivity(intent);
    }
}
