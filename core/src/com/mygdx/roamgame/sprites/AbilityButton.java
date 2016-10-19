package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.roamgame.RoamGame;

/**
 * Created by Justin on 2016-10-18.
 */
public class AbilityButton {

    private Sprite skin;
    public static float SCALE_RATIO = 1080f / Gdx.graphics.getWidth();

    public static Sprite createScaledSprite(Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        sprite.setSize(sprite.getWidth() / SCALE_RATIO,
                sprite.getHeight() / SCALE_RATIO);
        return sprite;
    }


    public AbilityButton(Texture texture) {
        skin = createScaledSprite(texture);
        skin.setPosition(0.97f*Gdx.graphics.getWidth() - skin.getWidth(), 0.03f*Gdx.graphics.getHeight());

    }

    public void setTexture(Texture texture)
    {
        skin = createScaledSprite(texture);
        skin.setPosition(0.97f*Gdx.graphics.getWidth() - skin.getWidth(), 0.03f*Gdx.graphics.getHeight());
    }

    public void update (SpriteBatch batch) {

        skin.draw(batch); // draw the button
    }

    public boolean checkIfClicked (float ix, float iy) {
        if (ix > skin.getX() && ix < skin.getX() + skin.getWidth()) {
            if (iy > skin.getY() && iy < skin.getY() + skin.getHeight()) {
                // the button was clicked, perform an action
                System.out.println("Button clicked !");
                return true;
            }
        }
        return false;
    }
}
