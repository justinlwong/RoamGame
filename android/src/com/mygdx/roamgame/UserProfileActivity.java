package com.mygdx.roamgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final EditText et_uname = (EditText) findViewById(R.id.et_uname);
        et_uname.setText("User Demo");
        final EditText et_fname = (EditText) findViewById(R.id.et_fname);
        et_fname.setText("John");
        final EditText et_lname = (EditText) findViewById(R.id.et_lname);
        et_lname.setText("Smith");
        final EditText et_age = (EditText) findViewById(R.id.et_age);
        et_age.setText("27");
        final EditText et_gender = (EditText) findViewById(R.id.et_gender);
        et_gender.setText("Male");
        Button submitButton = (Button)findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save user profile info
                String username = et_uname.getText().toString();
                String fname = et_fname.getText().toString();
                String lname = et_lname.getText().toString();
                String gender = et_gender.getText().toString();
                String age = et_age.getText().toString();

                if (username.isEmpty() || fname.isEmpty() || lname.isEmpty() || gender.isEmpty() || age.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields.", Toast.LENGTH_LONG).show();
                } else {
                    Integer ageI = Integer.parseInt(et_age.getText().toString());
                    boolean val = MenuActivity.userDB.insertProfile(username, fname, lname, gender, ageI);
                    if (!val) {
                        Toast.makeText(getApplicationContext(), "User name already chosen, please choose another name", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "User profile created successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });
    }
}
