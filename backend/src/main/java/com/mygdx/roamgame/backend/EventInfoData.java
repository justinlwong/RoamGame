package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class EventInfoData {

    @Id
    Long id;
    Long gameId;
    String userName;
    Long timestamp;
    int levelNo;
    int eventType;
    Long currentHealth;
    Long currentScore;
    Long currentLevelScore;
    int barrelDecision;
    int hazardType;
    int distanceFromExit;
    int levelDuration;

    public EventInfoData(Long gameId, String userName, Long t, int levelNo, int eT, Long cH, Long cS, Long cLS,  int bD, int hT, int dFE, int levelDuration) {
        this.gameId = gameId;
        this.userName = userName;
        this.timestamp = t;
        this.levelNo = levelNo;
        this.eventType = eT;
        this.currentHealth = cH;
        this.currentScore = cS;
        this.currentLevelScore = cLS;
        this.barrelDecision = bD;
        this.hazardType = hT;
        this.distanceFromExit = dFE;
        this.levelDuration = levelDuration;
    }

}
