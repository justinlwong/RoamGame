package com.mygdx.roamgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.roamgame.RoamGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = RoamGame.WIDTH;
		config.height = RoamGame.HEIGHT;
		config.title = RoamGame.TITLE;
		new LwjglApplication(new RoamGame(), config);
	}
}
