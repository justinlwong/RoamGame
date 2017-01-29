package com.mygdx.roamgame.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Justin on 2016-08-30.
 */
@Entity
public class RegisteredUserIDData{

    @Id
    public Long tag;
    @Index
    public int userID;


    public RegisteredUserIDData() {

    }

    public void setUserID(int id) {userID = id;}
    public int getUserID() {return userID;}

}
