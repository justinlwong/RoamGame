package com.mygdx.roamgame;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.roamgame.RoamGame;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;



public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new RoamGame(), config);

	}
}
