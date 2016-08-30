package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.roamgame.States.PlayState;

import java.util.ArrayList;

/**
 * Created by Justin on 2016-02-28.
 */
public class Food {
    private Texture candyShop;
    public static int foodTypeCount = 5;
    private Array<TextureRegion> foodTypeArray;
    private TextureRegion barrel;


    public Food() {
        candyShop = new Texture("candyshop.png");
        foodTypeArray = new Array<TextureRegion>(foodTypeCount);

        foodTypeArray.add(new TextureRegion(candyShop, 4* PlayState.SPRITE_SPACING, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2));
        foodTypeArray.add(new TextureRegion(candyShop, 2* PlayState.SPRITE_SPACING, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2));
        foodTypeArray.add(new TextureRegion(candyShop, 6* PlayState.SPRITE_SPACING, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2));
        foodTypeArray.add(new TextureRegion(candyShop, 8* PlayState.SPRITE_SPACING, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2));
        foodTypeArray.add(new TextureRegion(candyShop, 10* PlayState.SPRITE_SPACING, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2));

        barrel = new TextureRegion(candyShop, 0, 18*PlayState.SPRITE_SPACING, PlayState.SPRITE_SPACING*2, PlayState.SPRITE_SPACING*2);

    }

    public TextureRegion getFoodTexture(int foodType) {
        if (foodType >=0 && foodType <foodTypeCount)
            return foodTypeArray.get(foodType);
        else
            return foodTypeArray.get(0);
    }

    public TextureRegion getBarrel()
    {
        return barrel;
    }

    public void dispose() {
        candyShop.dispose();
    }


}
