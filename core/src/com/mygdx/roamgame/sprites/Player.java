package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.roamgame.RoamGame;

/**
 * Created by Justin on 2016-02-27.
 */
public class Player {

    public enum state
    {
        standby,
        walking
    }

    // special directions for player (Diagonal Movement)
    public enum dir
    {
        left,
        right,
        up,
        down,
        leftup,
        leftdown,
        rightup,
        rightdown
    }

    private int gameHeight;
    private int gameWidth;
    private Vector3 position;
    public Vector3 oldPosition;
    private Vector3 speed;
    private Rectangle bounds;
    Texture texture;
    Texture gooTexture;
    private PlayerAnimation walkAnimation;
    private PlayerAnimation gooWalkAnimation;
    dir curDir;

    private boolean isSlowed;
    private boolean isBoosted;

    static float speedmax = 360f;

    static float slowDamping = 0.98f;

    static float diagspeedmax = (float)Math.sqrt((float) (Math.pow(speedmax, 2)/2));

    static float curspeedmax;
    static float curdiagspeedmax;
    static float damping = 0.7f;
    dir d;
    state st;

    private long maxSlowedTime = 4000;
    private long startChangeSpeedTime;
    private float boostfactor;


    public Player(int x, int y, int height, int width, Texture person, Texture goo)
    {
        oldPosition = new Vector3(x, y, 0);
        position = new Vector3(x, y, 0);
        speed = new Vector3(0, 0, 0);
        d = dir.up;

        gameHeight = height;
        gameWidth = width;

        isSlowed = false;

        st = state.standby;
        texture = person;
        gooTexture = goo;
        walkAnimation = new PlayerAnimation(new TextureRegion(texture), 4, 1.0f);
        gooWalkAnimation =  new PlayerAnimation(new TextureRegion(gooTexture), 4, 2.0f);
        bounds = new Rectangle(x+5, y+5, texture.getWidth()/6, texture.getHeight()/12);
        curDir = dir.up;
        curspeedmax = speedmax;
        curdiagspeedmax = diagspeedmax;
    }

    public void resume()
    {
        walkAnimation = new PlayerAnimation(new TextureRegion(texture), 4, 1.0f);
        gooWalkAnimation =  new PlayerAnimation(new TextureRegion(gooTexture), 4, 2.0f);
    }

    public void keepMoving(dir d)
    {
        curDir = d;
    }

    public void slowPlayer ()
    {
        if (isSlowed == false && isBoosted != true) {
            Gdx.input.vibrate(new long [] {0, 400, 200, 400}, -1);
            isSlowed = true;
            startChangeSpeedTime = TimeUtils.millis();
        }

    }

    public void boostPlayer(float factor)
    {

        isSlowed = false;
        isBoosted = true;
        boostfactor = factor;
        startChangeSpeedTime = TimeUtils.millis();

    }

    public float getSpeedSum(){return Math.abs(speed.x) + Math.abs(speed.y);}

    public void update (float dt) {
        if (isBoosted)
        {
            curspeedmax = speedmax*boostfactor;
            curdiagspeedmax = diagspeedmax*boostfactor;

            if ((TimeUtils.millis() - startChangeSpeedTime) > maxSlowedTime)
            {
                isBoosted = false;
                curspeedmax = speedmax;
                curdiagspeedmax = diagspeedmax;
            }
        }
        else if (isSlowed)
        {
            curspeedmax *= slowDamping;
            curdiagspeedmax *= slowDamping;

            if (curspeedmax < 30f || curdiagspeedmax < 30f)
            {
                curspeedmax = 30f;
                curdiagspeedmax = 30f;
            }

            if ((TimeUtils.millis() - startChangeSpeedTime) > maxSlowedTime)
            {
                isSlowed = false;
                curspeedmax = speedmax;
                curdiagspeedmax = diagspeedmax;
            }
        }

        oldPosition.x = position.x;
        oldPosition.y = position.y;

        speed.scl(dt);
        position.add(speed);
        speed.scl(1/dt);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        speed.x *= damping;
        speed.y *= damping;

        if (Math.abs(speed.x) < 0.5f && Math.abs(speed.y) < 0.5f)
        {
            speed.x = 0;
            speed.y = 0;
            st = state.standby;
        }
        if (isSlowed == false) {
            walkAnimation.update(dt, st, d);
        } else
        {
            gooWalkAnimation.update(dt, st, d);
        }

        bounds.setPosition(position.x+5, position.y+5);

        if (position.x < 0)
            position.x = 0;

        if (position.x > (gameWidth - getTexture().getRegionWidth()))
            position.x = gameWidth- getTexture().getRegionWidth();

        if (position.y < 0)
            position.y = 0;

        if (position.y > (gameHeight - getTexture().getRegionHeight()))
            position.y = gameHeight - getTexture().getRegionHeight();
    }

    public void setPosition (Vector3 newPosition)
    {
        position.x = newPosition.x;
        position.y = newPosition.y;
    }


    public void moveUp()
    {
        st = state.walking;
        speed.x = 0;
        speed.y = curspeedmax;
        d = dir.up;
    }

    public void moveDown()
    {
        st = state.walking;
        speed.x = 0;
        speed.y = -curspeedmax;
        d = dir.down;
    }

    public void moveLeft()
    {
        st = state.walking;
        speed.x = -curspeedmax;
        speed.y = 0;
        d = dir.left;
    }

    public void moveRight()
    {
        st = state.walking;
        speed.y = 0;
        speed.x = curspeedmax;
        d = dir.right;
    }

    public void moveLeftUp()
    {
        st = state.walking;
        speed.y = curdiagspeedmax;
        speed.x = -curdiagspeedmax;
        d = dir.leftup;
    }

    public void moveLeftDown()
    {
        st = state.walking;
        speed.y = -diagspeedmax;
        speed.x = -diagspeedmax;
        d = dir.leftdown;
    }

    public void moveRightUp()
    {
        st = state.walking;
        speed.y = diagspeedmax;
        speed.x = diagspeedmax;
        d = dir.rightup;
    }

    public void moveRightDown()
    {
        st = state.walking;
        speed.y = -diagspeedmax;
        speed.x = diagspeedmax;
        d = dir.rightdown;
    }

//    public void move(float x, float y)
//    {
//        System.out.println("coord " + x + " " + y + "pos "+position.x + " " + position.y);
//        if (Math.abs(position.x - x) > Math.abs(position.y - y)) {
//            if (x < position.x) {
//                moveLeft();
//                curDir = dir.left;
//            } else {
//                moveRight();
//                curDir = dir.right;
//            }
//        } else {
//            if (y < position.y) {
//                moveDown();
//                curDir = dir.up;
//            } else {
//                moveUp();
//                curDir = dir.down;
//            }
//        }
//    }


    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {
        if (isSlowed == true && (isBoosted == false)) {
            return gooWalkAnimation.getFrame(st, d);
        }
        return walkAnimation.getFrame(st, d);
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose()
    {
        //texture.dispose();
        //gooTexture.dispose();
    }
}