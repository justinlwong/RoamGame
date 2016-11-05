package com.mygdx.roamgame;

/**
 * Created by Justin on 2016-11-01.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreArrayAdapter extends ArrayAdapter<Score> {
    private final Context context;
    private final ArrayList<Score> values;

    public ScoreArrayAdapter(Context context, ArrayList<Score> scores) {
        super(context, R.layout.scores_list, scores);
        this.context = context;
        this.values = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.scores_list, null);
        }

        Score p = getItem(position);

        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView score = (TextView) v.findViewById(R.id.score);

            if (name != null) {
                name.setText(p.name);
            }

            if (score != null) {
                score.setText(String.valueOf(p.score));
            }


        }

        return v;
    }
}
