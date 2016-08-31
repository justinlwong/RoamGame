package com.mygdx.roamgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class IUSurveyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iusurvey);

        ListView gad7ListView = (ListView) findViewById(R.id.ius_listview);
        ArrayList<String> qList = new ArrayList<String>();
        qList.add(getResources().getString(R.string.IUS_Q1));
        qList.add(getResources().getString(R.string.IUS_Q2));
        qList.add(getResources().getString(R.string.IUS_Q3));
        qList.add(getResources().getString(R.string.IUS_Q4));
        qList.add(getResources().getString(R.string.IUS_Q5));
        qList.add(getResources().getString(R.string.IUS_Q6));
        qList.add(getResources().getString(R.string.IUS_Q7));
        qList.add(getResources().getString(R.string.IUS_Q8));
        qList.add(getResources().getString(R.string.IUS_Q0));
        qList.add(getResources().getString(R.string.IUS_Q10));
        qList.add(getResources().getString(R.string.IUS_Q11));
        qList.add(getResources().getString(R.string.IUS_Q12));
        qList.add(getResources().getString(R.string.IUS_Q13));
        qList.add(getResources().getString(R.string.IUS_Q14));
        qList.add(getResources().getString(R.string.IUS_Q15));
        qList.add(getResources().getString(R.string.IUS_Q16));
        qList.add(getResources().getString(R.string.IUS_Q17));
        qList.add(getResources().getString(R.string.IUS_Q18));
        qList.add(getResources().getString(R.string.IUS_Q19));
        qList.add(getResources().getString(R.string.IUS_Q20));
        qList.add(getResources().getString(R.string.IUS_Q21));
        qList.add(getResources().getString(R.string.IUS_Q22));
        qList.add(getResources().getString(R.string.IUS_Q23));
        qList.add(getResources().getString(R.string.IUS_Q24));
        qList.add(getResources().getString(R.string.IUS_Q25));
        qList.add(getResources().getString(R.string.IUS_Q26));
        qList.add(getResources().getString(R.string.IUS_Q27));

        SurveyItemAdapter adapter = new SurveyItemAdapter(getApplicationContext(), qList);
        gad7ListView.setAdapter(adapter);

        Button submitIUS = (Button)findViewById(R.id.ius_submit);
        submitIUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Thank you for submitting your answers!", Toast.LENGTH_LONG).show();
                finish();
                Intent userProfileIntent = new Intent(getApplicationContext(), GAD7SurveyActivity.class);
                startActivity(userProfileIntent);
            }
        });
    }
}
