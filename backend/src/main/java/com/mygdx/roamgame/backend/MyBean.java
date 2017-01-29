package com.mygdx.roamgame.backend;

/** The object model for the data we are sending through endpoints */
public class MyBean {

    private String myData;
    private int gameID;
    private int userID;

    public String getData() {
        return myData;
    }

    public void setData(String data) {
        myData = data;
    }

    public void setIDData(int data) {gameID = data;}

    public int getIDData() {return gameID;}

    public void setUserID(int id) {userID = id;}
    public int getUserID() {return userID;}
}