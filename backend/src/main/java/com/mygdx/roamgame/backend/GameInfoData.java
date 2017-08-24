package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class GameInfoData {

    @Id
    public Long id;
    @Index
    public String user;
    @Index
    public Long score;
    @Index
    public Long gameDuration;
    @Index
    public int inputFrequency;
    @Index
    public String timestamp;
    @Index
    public int isDemo;

    public GameInfoData(Long gameID, String u, Long s, Long gd, int inputf, String ts, int isD) {
        id = gameID;
        user = u;
        score = s;
        gameDuration = gd;
        inputFrequency = inputf;
        timestamp = ts;
        isDemo = isD;
    }

}
