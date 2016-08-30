package com.mygdx.roamgame.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.roamgame.RoamGame;

/**
 * Created by Justin on 2016-02-21.
 */
public class MenuState extends State {

    private Texture background;
    private Texture playBtn;
    public MenuState(GameStateManager gsm)
    {
        super(gsm);
        cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);
        background = new Texture("menu.png");
        playBtn = new Texture("playbtn.png");
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched())
        {
            gsm.set(new PlayState(gsm));
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb, SpriteBatch hb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0, 0);
        sb.draw(playBtn, cam.position.x - playBtn.getWidth()/2, cam.position.y);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
    }


}
