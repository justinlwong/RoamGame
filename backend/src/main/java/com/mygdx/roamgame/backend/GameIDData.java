package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class GameIDData {

    @Id
    public Long tag;
    @Index
    public int gameID;


    public GameIDData(int id) {
        gameID = id;

    }

    public int getID() {
        return gameID;
    }

    public void incrementID() {
        gameID = gameID + 1;
    }

}
