package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class GameInfoData {

    @Id
    Long id;
    String user;
    Long score;

    public GameInfoData() {}

}
