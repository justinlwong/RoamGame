package com.mygdx.roamgame;

import com.badlogic.gdx.input.GestureDetector;

/**
 * Created by Justin on 2016-09-24.
 */
public class MovementGestureDetector extends GestureDetector {
    public interface DirectionListener {
        void onLeft();
        void onRight();
        void onUp();
        void onDown();
        void onLeftUp();
        void onLeftDown();
        void onRightUp();
        void onRightDown();
        //void onTouched(float x, float y);
    }

    public MovementGestureDetector(DirectionListener directionListener) {
        super(new DirectionGestureListener(directionListener));
    }

    private static class DirectionGestureListener extends GestureAdapter{
        DirectionListener directionListener;

        public DirectionGestureListener(DirectionListener directionListener){
            this.directionListener = directionListener;
        }

//        @Override
//        public boolean touchDown(float x, float y, int count, int button)
//        {
//            directionListener.onTouched(x, y);
//            return super.touchDown(x, y, count, button);
//        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            //System.out.println("Deltas " + deltaX + " " +deltaY);
            if (Math.abs(deltaX) > (Math.abs(deltaY) + 2))
            {
                if (deltaX > 0) {
                    directionListener.onRight();
                }
                else {
                    directionListener.onLeft();
                }
            } else if (Math.abs(deltaY) > (Math.abs(deltaX) + 2)) {
                if (deltaY > 0) {
                    directionListener.onDown();
                }
                else {
                    directionListener.onUp();
                }

            }
//            else
//            {
//                // diagonal movement
//                if (deltaX > 1 && deltaY > 1) {
//                    directionListener.onRightDown();
//                }
//                else if (deltaX < -1 && deltaY < -1)
//                {
//                    directionListener.onLeftUp();
//                }
//                else if (deltaX < -1 && deltaY > 1)
//                {
//                    directionListener.onLeftDown();
//                }
//                else if (deltaX > 1 && deltaY < -1)
//                {
//                    directionListener.onRightUp();
//                }
//            }
            return super.pan(x, y, deltaX, deltaY);
        }

    }

}
