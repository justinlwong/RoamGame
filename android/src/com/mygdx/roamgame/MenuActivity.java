package com.mygdx.roamgame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.mygdx.roamgame.backend.myApi.MyApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class MenuActivity extends Activity implements OnTaskCompleted {

    public class VerifyAsyncTask extends AsyncTask<Pair<Context,Integer>, Void, Integer> {
        //private static MyApi myApiService = null;
        private Context context;
        ProgressDialog progDailog = new ProgressDialog(MenuActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog.setMessage("Verifying...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progDailog.setCancelable(true);
            progDailog.show();
        }


        @Override
        protected Integer doInBackground(Pair<Context,Integer>... params) {
            if(MenuActivity.myApiService == null) {  // Only do this once
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

                MenuActivity.myApiService = builder.build();
            }

            //System.out.println("background");
            context = params[0].first;
            Integer cID = params[0].second;




            try {
                //return MenuActivity.myApiService.sayHi(name).execute().getData();
                return MenuActivity.myApiService.verifyID(cID).execute().getUserID();
            } catch (IOException e) {
                return -2;
            }

        }



        @Override
        protected void onPostExecute(Integer result) {
            if (progDailog.isShowing()) {
                progDailog.dismiss();
            }
            System.out.println("toast print");
            if (result == -1) {
                Toast.makeText(context, "Invalid ID! Please try again!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Verified " + String.valueOf(result) + "!", Toast.LENGTH_LONG).show();
                showButton();

            }

        }
    }

    Context context;
    public static RoamGameSQLiteHelper userDB;
    public static MyApi myApiService = null;
    public static int VERSION = 2;
    public static boolean startVerifyTask = false;
    public static int cID = -1;

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("resumed");
        hideButton();
        addDatatoLocalDB();

        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Justin"));

        if (startVerifyTask == true)
        {
            new VerifyAsyncTask().execute(new Pair<Context, Integer>(context, new Integer(cID)));
            startVerifyTask = false;
        }


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
        String filepath = s + "/files/gameInfoLog_"+String.valueOf(VERSION)+".txt";

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

    public void hideButton()
    {
        Button enterGame = (Button)findViewById(R.id.startGameButton);
        enterGame.setVisibility(View.GONE);
    }

    public void showButton()
    {
        Button enterGame = (Button)findViewById(R.id.startGameButton);
        enterGame.setVisibility(View.VISIBLE);
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



    @Override
    public void onTaskCompleted() {
        showButton();
    }
}
