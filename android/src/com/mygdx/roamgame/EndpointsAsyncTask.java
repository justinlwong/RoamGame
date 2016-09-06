package com.mygdx.roamgame;

/**
 * Created by Justin on 2016-08-29.
 */
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Pair;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.roamgame.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static MyApi myApiService = null;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        if(myApiService == null) {  // Only do this once
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

        System.out.println("background");
        context = params[0].first;
        String name = params[0].second;

        // Parse file
        //System.out.println("available: " + String.valueOf(Gdx.files.isLocalStorageAvailable()));
        //Environment.get
        //Environment.get
        String returnStr;

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
            File file = new File(filepath);
            String log = br.readLine();
            String userName = MenuActivity.userDB.getUserName();
            if (userName == null)
                userName = "JohnDoe";
            Random rand = new Random();
            int gameID = rand.nextInt(99999) + 1;
            while (log != null) {
                String[] entries = log.split(" ");
                System.out.println(String.valueOf(entries.length));
                if (entries != null && entries.length > 1) {

                    if (entries[0].compareTo("game") == 0) {
                        long score = Long.parseLong(entries[1]);
                        long gameDuration = Long.parseLong(entries[2])/1000;
                        int inputFrequency = Integer.parseInt(entries[3]);
                        System.out.println("Score: " + String.valueOf(score));

                        //GameInfoData gInfo = new GameInfoData(name, score, gameDuration, inputFrequency);
                        myApiService.uploadGameData(gameID, userName, (int) score, (int) gameDuration, inputFrequency).execute();
                    } else if (entries[0].compareTo("event") == 0) {


                        System.out.println("Event logged");
                        long timeStamp = Long.parseLong(entries[1])/1000;
                        int levelNo = Integer.parseInt(entries[2]);
                        int evenType = Integer.parseInt(entries[3]);
                        float cHealth = Float.parseFloat(entries[4]);
                        long cScore = Long.parseLong(entries[5]);
                        long cLScore = Long.parseLong(entries[6]);
                        int barrelDecision = Integer.parseInt(entries[7]);
                        int hazardType = Integer.parseInt(entries[8]);
                        int distanceFromExit = Integer.parseInt(entries[9]);
                        int levelDuration = Integer.parseInt(entries[10]);

                        //GameInfoData gInfo = new GameInfoData(name, score, gameDuration, inputFrequency);
                        myApiService.uploadEventData(gameID, userName,(int)timeStamp, levelNo, evenType, (int)cHealth, (int)cScore, (int)cLScore, barrelDecision, hazardType, distanceFromExit, levelDuration).execute();
                    }


                }

                log = br.readLine();

            }

            returnStr = "Added "+userName+"'s entry to datastore!";
            br.close();
            if(file.delete()){
                System.out.println(file.getName() + " is deleted!");
            }else{
                System.out.println("Delete operation is failed.");
            }
            return returnStr;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            return myApiService.sayHi(name).execute().getData();
        } catch (IOException e) {
            return e.getMessage();
       }

    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("toast print");
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}