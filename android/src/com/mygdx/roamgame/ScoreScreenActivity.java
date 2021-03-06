package com.mygdx.roamgame;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Justin on 2016-11-01.
 */
public class ScoreScreenActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ArrayList<Score> scores =  MenuActivity.userDB.getHighScores();
        Collections.sort(scores);
        List<Score> top10 = new ArrayList<Score>();
        top10 = scores.subList(0, Math.min(10, scores.size()));
        //Log.d("scores", scores.get(0).name);
        final ScoreArrayAdapter adapter = new ScoreArrayAdapter(this, top10);

        final ListView listview = (ListView) findViewById(R.id.listview);


        listview.setAdapter(adapter);

    }
}
