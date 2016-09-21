package com.mygdx.roamgame.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by Justin on 2016-02-21.
 */
public class GameStateManager {

    private Stack<State> states;

    public GameStateManager() {
        states = new Stack<State>();
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop (State state)
    {
        states.pop().dispose();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt) {
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb, SpriteBatch hb) {

        states.peek().render(sb, hb);
    }

    public void resume()
    {
        states.peek().resume();
    }

}
