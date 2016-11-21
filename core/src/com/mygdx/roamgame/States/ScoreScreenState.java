package com.mygdx.roamgame.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.roamgame.RoamGame;

/**
 * Created by Justin on 2016-03-02.
 */
public class ScoreScreenState extends State{

    Preferences prefs;
    BitmapFont font;
    GlyphLayout layout;

    private Texture background;
    public ScoreScreenState(GameStateManager gsm)
    {
        super(gsm);
        cam.setToOrtho(false, RoamGame.WIDTH, RoamGame.HEIGHT);
        prefs = Gdx.app.getPreferences("Stats");
        layout = new GlyphLayout();
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.isTouched())
        {
            Gdx.app.exit();
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
        sb.end();

        int score = prefs.getInteger("score", 0);
        int factor1 = prefs.getInteger("factor1", 0);
        int factor2 = prefs.getInteger("factor2", 0);

        float screenScale = 2f*Gdx.graphics.getWidth()/(RoamGame.WIDTH);

        hb.begin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("CaviarDreams.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int)(screenScale*( 15 ));
        //System.out.println(alpha);
        parameter.color = new Color(255, 255, 255, 255);
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:/";

        font = generator.generateFont(parameter);

        generator.dispose();

        font.draw(hb, "Game Over!", 0.1f * Gdx.graphics.getWidth(), 0.95f * Gdx.graphics.getHeight());

        font.draw(hb, "Score: " + score, 0.1f*Gdx.graphics.getWidth(), 0.85f * Gdx.graphics.getHeight());
//        font.draw(hb, "IU Factor 1 Score: " + factor1 + " / 75", 0.1f*Gdx.graphics.getWidth(), 0.80f * Gdx.graphics.getHeight());
//        font.draw(hb, "IU Factor 2 Score: " + factor2 + " / 60", 0.1f*Gdx.graphics.getWidth(), 0.75f * Gdx.graphics.getHeight());

//        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//        String scoreText = "Score : " + String.valueOf(score);
//        layout.setText(font, scoreText);
//        float width = layout.width;
//        float height = layout.height;
//        font.draw(hb, scoreText , Gdx.graphics.getWidth()/2 - width/2, 0.95f*Gdx.graphics.getHeight() - height);

//        Label.LabelStyle textStyle;
//        Label text;
//
//        textStyle = new Label.LabelStyle();
//        textStyle.font = font;
//
//        text = new Label("Game Over! Hope you had fun!", textStyle);
//        float height = text.getPrefHeight();
//        float width = text.getPrefWidth();
//        text.setFontScale(screenScale, screenScale);
//        text.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, 0.95f * Gdx.graphics.getHeight() - height);
//
//        text.draw(hb, 1f);
//
//        text = new Label("Score : " + String.valueOf(score), textStyle);
//        height = text.getPrefHeight();
//        width = text.getPrefWidth();
//        text.setFontScale(screenScale, screenScale);
//        text.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, 0.93f * Gdx.graphics.getHeight() - height);
//        text.draw(hb, 1f);
//
//        text = new Label("IU Factor 1 Score out of 70 : " + String.valueOf(factor1), textStyle);
//        height = text.getPrefHeight();
//        width = text.getPrefWidth();
//        text.setFontScale(screenScale, screenScale);
//        text.setPosition(Gdx.graphics.getWidth() / 2 - width / 2, 0.85f * Gdx.graphics.getHeight() - height);
//        text.draw(hb, 1f);
//
//        text = new Label("IU Factor 2 Score out of 70 : " + String.valueOf(factor2), textStyle);
//        height = text.getPrefHeight();
//        width = text.getPrefWidth();
//        text.setFontScale(screenScale, screenScale);
//        text.setPosition(Gdx.graphics.getWidth()/2 - width/2, 0.83f*Gdx.graphics.getHeight() - height);
//
//        text.draw(hb, 1f);
        hb.end();
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
