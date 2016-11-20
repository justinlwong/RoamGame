package com.mygdx.roamgame;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.mygdx.roamgame.RoamGame;
import com.mygdx.roamgame.States.PlayState;
import com.mygdx.roamgame.backend.myApi.MyApi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import java.io.IOException;


public class AndroidLauncher extends AndroidApplication {

//	private static MyApi myApiService = null;
//
//	public class loadApiAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
//
//		private Context context;
//
//		@Override
//		protected String doInBackground(Pair<Context, String>... params) {
//			if(myApiService == null) {  // Only do this once
//				MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
//						new AndroidJsonFactory(), null)
//						//.setRootUrl("http://10.0.2.2:8080/_ah/api/")
//						// options for running against local devappserver
//						// - 10.0.2.2 is localhost's IP address in Android emulator
//						// - turn off compression when running against local devappserver
//						.setRootUrl("https://roamgame-141903.appspot.com/_ah/api/");
//				//.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//				//    @Override
//				//    public void initialize(AbstractGoogleClientRequest abstractGoogleClientRequest) throws IOException {
//				//        abstractGoogleClientRequest.setDisableGZipContent(true);
//				//    }
//				//});
//
//				// end options for devappserver
//
//				myApiService = builder.build();
//			}
//
//			System.out.println("background");
//			context = params[0].first;
//			String name = params[0].second;
//
////			try {
////				return myApiService.sayHi(name).execute().getData();
////			} catch (IOException e) {
////				return e.getMessage();
////			}
//
//
//			String thing = "whatever";
//			return thing;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			System.out.println("toast print");
//			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
//
//			AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//			RoamGame instance = new RoamGame();
//			if (myApiService != null)
//			    instance.setApi(myApiService);
//			initialize(instance, config);
//
//		}
//	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


		//RoamGame instance = new RoamGame();
		initialize(new RoamGame(), config);

		//new loadApiAsyncTask().execute(new Pair<Context, String>(this, "Justin"));

	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


		//RoamGame instance = new RoamGame();
		initialize(new RoamGame(), config);
	}


}
