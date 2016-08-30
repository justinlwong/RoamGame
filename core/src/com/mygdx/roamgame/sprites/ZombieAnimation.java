package com.mygdx.roamgame.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.roamgame.States.PlayState;

/**
 * Created by Vinu on 2016-02-29.
 */
public class ZombieAnimation {

    private Array<TextureRegion> walkDownFrames;
    private Array<TextureRegion> walkUpFrames;
    private Array<TextureRegion> walkLeftFrames;
    private Array<TextureRegion> walkRightFrames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;

    public ZombieAnimation(TextureRegion region, int frameCount, float cycleTime) {
        int frameWidth = region.getRegionWidth() / frameCount;
        int frameHeight = region.getRegionHeight() / frameCount;

        walkDownFrames = new Array<TextureRegion>();
        for (int i=0; i < frameCount; i++) {
            walkDownFrames.add(new TextureRegion(region, i * frameWidth, 0, frameWidth, frameHeight));
        }

        walkLeftFrames = new Array<TextureRegion>();
        for (int i=0; i < frameCount; i++) {
            walkLeftFrames.add(new TextureRegion(region, i * frameWidth, frameHeight, frameWidth, frameHeight));
        }

        walkRightFrames = new Array<TextureRegion>();
        for (int i=0; i < frameCount; i++) {
            walkRightFrames.add(new TextureRegion(region, i * frameWidth, 2*frameHeight, frameWidth, frameHeight));
        }

        walkUpFrames = new Array<TextureRegion>();
        for (int i=0; i < frameCount; i++) {
            walkUpFrames.add(new TextureRegion(region, i * frameWidth, 3*frameHeight, frameWidth, frameHeight));
        }


        this.frameCount = frameCount;
        maxFrameTime = cycleTime / frameCount;
        frame = 0;
    }

    public void update(float dt)
    {
            currentFrameTime += dt;
            if (currentFrameTime > maxFrameTime) {
                frame++;
                currentFrameTime = 0;
            }

            if (frame >= frameCount)
                frame = 0;
    }

    public TextureRegion getFrame(int dr) {
        if (dr == PlayState.UP)
            return walkUpFrames.get(frame);
        else if (dr == PlayState.DOWN)
            return walkDownFrames.get(frame);
        else if (dr == PlayState.LEFT)
            return walkLeftFrames.get(frame);
        else if (dr == PlayState.RIGHT)
            return walkRightFrames.get(frame);
        else
            return walkUpFrames.get(0);

    }
}
