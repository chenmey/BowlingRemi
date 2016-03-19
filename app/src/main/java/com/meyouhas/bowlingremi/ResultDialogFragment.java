package com.meyouhas.bowlingremi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Meyouhas on 04-Dec-15.
 */
public class ResultDialogFragment extends DialogFragment {

    private View fragmentView;
    private String playerName;
    private int playerIndex;
    private int invokedFrom;
    private int playerTeam;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        fragmentView = inflater.inflate(R.layout.score_fragment, null);
        final Bundle b = getArguments();
        playerName = b.getString("playerName");
        playerIndex = b.getInt("playerIndex");
        invokedFrom = b.getInt("invokedFrom");

        playerTeam = -1;
        if (invokedFrom == 3)
            playerTeam = b.getInt("playerTeam");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(fragmentView)
                // Add action buttons
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast toast;
                        EditText et = (EditText) (fragmentView.findViewById(R.id.score));
                        if(et == null){
                            Log.d("chenm","et is nulllllllllll");
                            return;
                        }
                        String scoreStr = et.getText().toString();
                        if (scoreStr.isEmpty()){
                            toast = Toast.makeText(getActivity(), "No score was given!", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        Integer score = Integer.parseInt(et.getText().toString());
                        if(score > 300 || score < 0) {
                            toast = Toast.makeText(getActivity(), "Score has to be between 0-300!", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }

                        switch (invokedFrom) {
                            case 1:
                                SinglesScoresActivity.class.cast(getActivity()).updatePlayerScore(playerIndex,score);
                                break;
                            case 2:
                                GameHistoryActivity.class.cast(getActivity()).savePlayerScore(playerIndex,score);
                                break;
                            case 3:
                                CouplesScoresActivity.class.cast(getActivity()).updatePlayerScoreInCouple(playerTeam, playerIndex,score);
                                break;

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ResultDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle("Set score for " + playerName);
        return builder.create();
    }

}
