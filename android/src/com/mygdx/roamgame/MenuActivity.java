package com.mygdx.roamgame;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.mygdx.roamgame.backend.myApi.MyApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class MenuActivity extends Activity {

    Context context;
    public static RoamGameSQLiteHelper userDB;
    public static MyApi myApiService = null;

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("resumed");

        addDatatoLocalDB();

        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Justin"));


    }

    public void addDatatoLocalDB()
    {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(s, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        s = p.applicationInfo.dataDir;
        String filepath = s + "/files/gameInfoLog.txt";

        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String log = br.readLine();
            String userName = MenuActivity.userDB.getUserName();
            if (userName == null)
                userName = "JohnDoe";
            Random rand = new Random();
            while (log != null) {
                String[] entries = log.split(" ");
                System.out.println(String.valueOf(entries.length));
                if (entries != null && entries.length > 1) {

                    if (entries[0].compareTo("game") == 0) {
                        long score = Long.parseLong(entries[1]);
                        String timestamp = (entries[4]);
                        System.out.println("Score: " + String.valueOf(score));

                        MenuActivity.userDB.insertScore(timestamp, userName, (int)score);

                        return;

                    }

                }

                log = br.readLine();

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;

        userDB = new RoamGameSQLiteHelper(getApplicationContext());

        Button enterGame = (Button)findViewById(R.id.startGameButton);
        enterGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchGame = new Intent(context, AndroidLauncher.class);
                startActivity(launchGame);
            }
        });

        Button userProfile = (Button)findViewById(R.id.entireProfileButton);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfileIntent = new Intent(context, UserProfileActivity.class);
                startActivity(userProfileIntent);
            }
        });

        Button scoresButton = (Button)findViewById(R.id.scoreScreenButton);
        scoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scoresIntent = new Intent(context, ScoreScreenActivity.class);
                startActivity(scoresIntent);
            }
        });

//        Button iusButton = (Button)findViewById(R.id.button_ius);
//        iusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent userProfileIntent = new Intent(context, IUSurveyActivity.class);
//                startActivity(userProfileIntent);
//            }
//        });

        /*Button gad7Button = (Button)findViewById(R.id.button_gad7);
        gad7Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfileIntent = new Intent(context, GAD7SurveyActivity.class);
                startActivity(userProfileIntent);
            }
        });*/

//        Button userGButton = (Button)findViewById(R.id.userGuideButton);
//        userGButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent userProfileIntent = new Intent(context, user_guide.class);
//                startActivity(userProfileIntent);
//            }
//        });

        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                //.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("https://roamgame-141903.appspot.com/_ah/api/");
        //.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
        //    @Override
        //    public void initialize(AbstractGoogleClientRequest abstractGoogleClientRequest) throws IOException {
        //        abstractGoogleClientRequest.setDisableGZipContent(true);
        //    }
        //});

        // end options for devappserver

        myApiService = builder.build();


    }

}
