package com.meyouhas.bowlingremi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Meyouhas on 20-Jan-16.
 */

abstract class RemiArrayAdapter extends ArrayAdapter<Player> {

    protected Context context;
    protected List<Player> objects;
    protected DataBaseST db;

    RemiArrayAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
        this.db = DataBaseST.getInstance();

    }

    View PrepareView(String PlayerName, Integer playerScore, Double gamesToPay, Double profit, boolean isEdit) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sum_list_view,null);

        TextView tv = (TextView) view.findViewById(R.id.playerNameSum);
        tv.setText(PlayerName);

        tv = (TextView) view.findViewById(R.id.playerScoreCell);
        if (playerScore != null)
            tv.setText(playerScore.toString());
        else
            tv.setText("Didn't play");

        if (isEdit) {
            tv.setTextColor(Color.RED);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);
        }

        tv = (TextView) view.findViewById(R.id.playerGamesPayed);
        if (gamesToPay != null) {
            String gamesToPayStr = String.format("%.2f",gamesToPay + 0.0);
            if (gamesToPay >= 0.0)
                gamesToPayStr = "+"+gamesToPayStr;
            tv.setText(gamesToPayStr);
        }
        else
            tv.setText(String.format("%.2f", 0.0));

        tv = (TextView) view.findViewById(R.id.playerProfit);
        if (profit != null) {
            String profitStr = String.format("%.2f",profit + 0.0);
            if (profit >= 0.0)
                profitStr = "+"+profitStr;
            tv.setText(profitStr);
        }
        else
            tv.setText(String.format("%.2f", 0.0));

        return view;
    }
}