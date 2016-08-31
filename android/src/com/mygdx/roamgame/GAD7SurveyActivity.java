package com.mygdx.roamgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class GAD7SurveyActivity extends Activity {
    public static final int GAD7_NUM_QS = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gad7);

        // setup the listview adapter
        ListView gad7ListView = (ListView) findViewById(R.id.gad7_listview);
        ArrayList<String> qList = new ArrayList<String>();
        qList.add(getResources().getString(R.string.GAD7_Q1));
        qList.add(getResources().getString(R.string.GAD7_Q2));
        qList.add(getResources().getString(R.string.GAD7_Q3));
        qList.add(getResources().getString(R.string.GAD7_Q4));
        qList.add(getResources().getString(R.string.GAD7_Q5));
        qList.add(getResources().getString(R.string.GAD7_Q6));
        qList.add(getResources().getString(R.string.GAD7_Q7));

        SurveyItemAdapter adapter = new SurveyItemAdapter(getApplicationContext(), qList);
        gad7ListView.setAdapter(adapter);

        Button submitGAD = (Button)findViewById(R.id.gad7_submit);
        submitGAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Thank you for submitting your answers!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
