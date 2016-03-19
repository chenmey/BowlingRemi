package com.meyouhas.bowlingremi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Meyouhas on 20-Jan-16.
 */
public class SumRemiArrayAdapter extends RemiArrayAdapter {

    public SumRemiArrayAdapter(Context context, int resource, List<Player> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = objects.get(position);
        String PlayerName = player.getName();
        Double gamesToPay = player.getTotalGames();
        Double profit = player.getTotalProfit();
        View view = PrepareView(PlayerName,0,gamesToPay,profit,false);
        view.findViewById(R.id.playerScoreCell).setVisibility(View.GONE);
        return view;
    }
}
