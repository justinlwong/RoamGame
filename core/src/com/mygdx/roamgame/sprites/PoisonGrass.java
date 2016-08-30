package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.roamgame.States.PlayState;

/**
 * Created by Vinu on 2016-03-20.
 */
public class PoisonGrass {
    private Texture rogueSheet;
    private TextureRegion redGrass;
    private TextureRegion blueGrass;
    private int ROGUE_SHEET_SPACING = 20;
    private int ROGUE_SHEET_TILE_SIZE = 16;


    public PoisonGrass() {
        rogueSheet = new Texture("roguelike-pack/Spritesheet/roguelikeSheet_transparent.png");
        blueGrass = new TextureRegion(rogueSheet, 28*ROGUE_SHEET_SPACING, 9*ROGUE_SHEET_SPACING, ROGUE_SHEET_TILE_SIZE, ROGUE_SHEET_TILE_SIZE);
        redGrass = new TextureRegion(rogueSheet, 29*ROGUE_SHEET_SPACING, 9*ROGUE_SHEET_SPACING, ROGUE_SHEET_TILE_SIZE, ROGUE_SHEET_TILE_SIZE);
    }

    public TextureRegion getRedGrass()
    {
        return redGrass;
    }

    public TextureRegion getBlueGrass()
    {
        return blueGrass;
    }

    public void dispose() {
        rogueSheet.dispose();
    }


}