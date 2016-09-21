package com.mygdx.roamgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.roamgame.States.GameStateManager;
import com.mygdx.roamgame.States.MenuState;
import com.mygdx.roamgame.backend.myApi.MyApi;

public class RoamGame extends ApplicationAdapter  {

	public enum AppState
	{
		PAUSE,
		RUN,
		RESUME,
		STOPPED
	}

	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;

	public static final String TITLE = "Roam Game";
	private GameStateManager gsm;
	private SpriteBatch batch;
	private SpriteBatch hudBatch;

	private static MyApi apiService;

	public void setApi(MyApi service)
	{
		apiService = service;
	}

	private AppState state = AppState.RUN;


	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		gsm = new GameStateManager();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		gsm.push(new MenuState(gsm));

		//Gdx.input.setInputProcessor(this);
		//Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void render () {
		switch (state)
		{
			case RUN:
//do suff here
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				gsm.update(Gdx.graphics.getDeltaTime());
				gsm.render(batch, hudBatch);
				break;
			case PAUSE:
//do stuff here

				break;
			case RESUME:

				break;

			default:
				break;
		}

	}

	@Override
	public void dispose() {
		super.dispose();

	}

	@Override
	public void pause()
	{
		this.state = AppState.PAUSE;
	}

	@Override
	public void resume()
	{
		gsm.resume();
		this.state = AppState.RUN;
	}



}
