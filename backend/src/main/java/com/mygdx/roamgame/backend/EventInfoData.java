package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class EventInfoData {

    @Id
    public Long id;
    @Index
    public Long gameId;
    @Index
    public String userName;
    @Index
    public Long timestamp;
    @Index
    public int levelNo;
    @Index
    public int eventType;
    @Index
    public Long currentHealth;
    @Index
    public Long currentScore;
    @Index
    public Long currentLevelScore;
    @Index
    public int barrelDecision;
    @Index
    public int hazardType;
    @Index
    public int distanceFromExit;
    @Index
    public int levelDuration;
    @Index
    public int closestZombieDistance;
    @Index
    public int exitDistance;

    public EventInfoData(Long gameId, String userName, Long t, int levelNo, int eT, Long cH, Long cS, Long cLS,  int bD, int hT, int dFE, int levelDuration, int closestZombieDistance, int exitDistance) {
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
        this.closestZombieDistance = closestZombieDistance;
        this.exitDistance = exitDistance;
    }

}
