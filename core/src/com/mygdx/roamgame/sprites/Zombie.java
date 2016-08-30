package com.mygdx.roamgame.sprites;

/**
 * Created by Vinu on 2016-02-28.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.roamgame.States.PlayState;

/**
 * Created by Justin on 2016-02-27.
 */
public class Zombie {


    private Vector3 position;
    public Vector3 oldPosition;
    public Vector3 speed;
    private Rectangle bounds;
    private Texture zombie;
    private ZombieAnimation zombieAnimation;
    private Sound flap;

    private float speedmax = 90f;
    private boolean turboOn = false;
    private int direction;
    private Sound boink;


    public Zombie(float x, float y, int dir, int type)
    {
        oldPosition = new Vector3(x, y, 0);
        position = new Vector3(x, y, 0);
        speed = new Vector3(0, 0, 0);


        if (type == 1) {
            speedmax = 180f;
            zombie = new Texture("witch.png");
        } else {
            zombie = new Texture("zombie.png");
        }
        zombieAnimation = new ZombieAnimation(new TextureRegion(zombie), 4, 1.0f);
        bounds = new Rectangle(x, y, zombie.getWidth()/4, zombie.getHeight()/4);
        direction = dir;
        boink = Gdx.audio.newSound(Gdx.files.internal("sfx_wing.ogg"));


    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        bounds.setPosition(position.x, position.y);
    }

    public void update (float dt) {

        oldPosition.x = position.x;
        oldPosition.y = position.y;
        speed.scl(dt);
        position.add(speed);
        speed.scl(1 / dt);

        zombieAnimation.update(dt);
        bounds.setPosition(position.x, position.y);
    }

    public void move(int dir)
    {
        float speedChosen = speedmax;

        if (turboOn)
            speedChosen  = 2*speedmax;
        if(dir == PlayState.UP) { // UP
            speed.x = 0;
            speed.y = speedChosen;
        }
        else if(dir == PlayState.DOWN) { // DOWN
            speed.x = 0;
            speed.y = -speedChosen;
        }
        else if(dir == PlayState.LEFT) { // LEFT
            speed.x = -speedChosen;
            speed.y = 0;
        }
        else if(dir == PlayState.RIGHT) { // RIGHT
            speed.y = 0;
            speed.x = speedChosen;
        }
        else {
            speed.x = 0;
            speed.y = 0;
        }
    }

    public Vector3 getPosition() {
        return position;
    }
    public TextureRegion getTexture() {
        return zombieAnimation.getFrame(direction);
    }

    public void setSpeedFaster()
    {
        speedmax += 5f;
    }

    public void setTurboOn(boolean tOn)
    {
        turboOn = tOn;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void playSound() {
        boink.play(0.25f);
    }


    public void dispose()
    {
        zombie.dispose();
        //flap.dispose();
    }
}