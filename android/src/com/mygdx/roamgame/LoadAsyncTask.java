//package com.mygdx.roamgame;
//
//import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.util.Pair;
//import android.widget.Toast;
//
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.extensions.android.json.AndroidJsonFactory;
//import com.googlecode.objectify.cmd.Query;
//import com.mygdx.roamgame.backend.GameInfoData;
//import com.mygdx.roamgame.backend.myApi.MyApi;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Random;
//
///**
// * Created by Justin on 2016-11-01.
// */
//public class LoadAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
//    private static MyApi myApiService = null;
//    private Context context;
//
//    @Override
//    protected String doInBackground(Pair<Context, String>... params) {
//        if(myApiService == null) {  // Only do this once
//            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    //.setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                    // options for running against local devappserver
//                    // - 10.0.2.2 is localhost's IP address in Android emulator
//                    // - turn off compression when running against local devappserver
//                    .setRootUrl("https://roamgame-141903.appspot.com/_ah/api/");
//            //.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//            //    @Override
//            //    public void initialize(AbstractGoogleClientRequest abstractGoogleClientRequest) throws IOException {
//            //        abstractGoogleClientRequest.setDisableGZipContent(true);
//            //    }
//            //});
//
//            // end options for devappserver
//
//            myApiService = builder.build();
//        }
//
//        System.out.println("background");
//        context = params[0].first;
//        String name = params[0].second;
//
//        //try {
//            //Query<GameInfoData> gInfo = myApiService.getScores(MenuActivity.userDB.getUserName());
//
//            //for (GameInfoData g : gInfo)
//            //{
//            //    Log.d("gameinfo", g.toString());
//            //}
//        //} catch (IOException e) {
//        //    e.printStackTrace();
//        //}
//
//
//        String resultStr = "loaded!";
//        return resultStr;
//
//
//
//
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        System.out.println("toast print");
//        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
//    }
//
//}
