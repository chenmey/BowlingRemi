package com.meyouhas.bowlingremi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Meyouhas on 07-Jan-16.
 */
class SumGameArrayAdapter extends RemiArrayAdapter {

    private Context context;
    private List<Player> objects;
    private DataBaseST db;
    private Integer gameNum;
    private boolean isEdit;

    public SumGameArrayAdapter(Context context, int resource, List<Player> objects, Integer gameNum) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
        this.db = DataBaseST.getInstance();
        this.gameNum = gameNum;
        this.isEdit = false;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = objects.get(position);
        String PlayerName = player.getName();

        Double gamesToPay = null;
        Double profit = null;
        Integer playerScore = null;
        PlayerGame playerGame = player.getGamesMap().get(gameNum);
        if (playerGame != null) {
            gamesToPay = playerGame.getGames();
            profit = playerGame.getProfit();
            playerScore = playerGame.getScore();
        }
        return PrepareView(PlayerName,playerScore,gamesToPay,profit, isEdit);
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public boolean isEdit() {
        return isEdit;
    }
}