package com.meyouhas.bowlingremi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ChooseActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_action);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Quit if back is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (DataBaseST.getInstance().getCurrentGameNum() > 0)
                alertUserCancelRemi();
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (DataBaseST.getInstance().getCurrentGameNum() > 0)
                    alertUserCancelRemi();
                else
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void alertUserCancelRemi() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setTitle("Warning!")
                .setMessage("This action will delete the current Remi PlayerGame!\nPlease consider using the Summary button")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }

    public void createSinglesGame(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        intent.putExtra("isCouplesGame",false);
        startActivity(intent);
    }

    public void Summary(View view) {
        if (DataBaseST.getInstance().getCurrentGameNum() > 0) {
            Intent intent = new Intent(this, SumRemiActivity.class);
            startActivity(intent);
        }
        else {
            Toast toast = Toast.makeText(this, "No games were played!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void createCouplesGame(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        intent.putExtra("isCouplesGame",true);
        startActivity(intent);
    }
}
