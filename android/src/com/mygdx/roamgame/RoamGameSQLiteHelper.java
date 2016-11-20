package com.mygdx.roamgame;

/**
 * Created by Justin on 2016-03-10.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class RoamGameSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROFILES = "profiles";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_AGE = "age";

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_GAMEID = "gameid";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_GAME_ID = "gameid";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_FINALSCORE = "finalscore";
    public static final String COLUMN_USER_EMAIL = "useremail";

    public static final String TABLE_LEVELS = "levels";
    public static final String COLUMN_LEVEL_ID = "levelid";
    public static final String COLUMN_LEVEL_GAME_ID = "levelgameid";
    public static final String COLUMN_LEVEL_SCORE = "levelgameid";
    public static final String COLUMN_LEVEL_FINAL_HEALTH= "levelfinalhealth";

    public static final String TABLE_LOGS= "logs";
    public static final String COLUMN_LOG_ID = "logid";
    public static final String COLUMN_LOG_LEVEL_ID = "loglevelid";
    public static final String COLUMN_LOG_TIMESTAMP = "timestamp";
    public static final String COLUMN_LOG_EVENT = "event";
    public static final String COLUMN_LOG_CURRENT_HEALTH = "currenthealth";
    public static final String COLUMN_LOG_CURRENT_SCORE = "currentscore";

    private static final String DATABASE_NAME = "profiles.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_PROFILES = "create table "
            + TABLE_PROFILES + "(" + COLUMN_EMAIL
            + " text primary key not null, " + /*COLUMN_PASSWORD
            + " text not null, " +*/ COLUMN_FIRST_NAME
            + " text not null, " + COLUMN_LAST_NAME
            + " text not null, " + COLUMN_GENDER
            + " INTEGER not null, " + COLUMN_AGE
            + " text not null);";

    private static final String DATABASE_CREATE_SCORES = "create table "
            + TABLE_SCORES + "(" + COLUMN_TIMESTAMP
            + " text primary key not null, " + COLUMN_USERNAME
            + " text not null, " + COLUMN_SCORE
            + " INTEGER not null);";


    private static final String DATABASE_CREATE_GAMES = "create table "
            + TABLE_GAMES+ "(" + COLUMN_GAME_ID
            + " INTEGER primary key autoincrement, " + COLUMN_DATE
            + " INTEGER not null, " + COLUMN_FINALSCORE
            + " INTEGER not null, " + COLUMN_USER_EMAIL
            + " text not null);";

    private static final String DATABASE_CREATE_LEVELS = "create table "
            + TABLE_LEVELS+ "(" + COLUMN_LEVEL_ID
            + " INTEGER primary key autoincrement, " + COLUMN_LEVEL_GAME_ID
            + " INTEGER not null, " + COLUMN_LEVEL_SCORE
            + " INTEGER not null, " + COLUMN_LEVEL_FINAL_HEALTH
            + " INTEGER not null);";

    private static final String DATABASE_CREATE_LOGS = "create table "
            + TABLE_LOGS+ "(" + COLUMN_LOG_ID
            + " INTEGER primary key autoincrement, " + COLUMN_LOG_LEVEL_ID
            + " INTEGER not null, " + COLUMN_LOG_TIMESTAMP
            + " INTEGER not null, " + COLUMN_LOG_EVENT
            + " text not null, " + COLUMN_LOG_CURRENT_HEALTH
            + " INTEGER not null, " + COLUMN_LOG_CURRENT_SCORE
            + " INTEGER not null);";

    public RoamGameSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_PROFILES);
        database.execSQL(DATABASE_CREATE_SCORES);
        /*database.execSQL(DATABASE_CREATE_GAMES);
        database.execSQL(DATABASE_CREATE_LOGS);
        database.execSQL(DATABASE_CREATE_LEVELS);*/

    }

    public boolean insertProfile (String email, /*String password,*/ String firstname, String lastname, String gender, Integer age){
        int retVal = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        //contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_FIRST_NAME, firstname);
        contentValues.put(COLUMN_LAST_NAME, lastname);
        contentValues.put(COLUMN_GENDER, gender);
        contentValues.put(COLUMN_AGE, age);
        retVal = (int)db.insertWithOnConflict(TABLE_PROFILES, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (retVal < 0)
            return false;
        else
            return true;
    }

    public boolean insertScore ( String timestamp, String name, Integer score)
    {
        int retVal = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIMESTAMP, timestamp);
        contentValues.put(COLUMN_USERNAME, name);
        contentValues.put(COLUMN_SCORE, score);

        retVal = (int)db.insertWithOnConflict(TABLE_SCORES, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);



        if (retVal < 0)
            return false;
        else
            return true;
    }

    public String getUserName ()
    {
        String userName = "John Doe";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultSet = db.rawQuery("Select * From " + TABLE_PROFILES, null);
        resultSet.moveToLast();
        int ind = resultSet.getColumnIndex(COLUMN_EMAIL);
        int rows = resultSet.getCount();
        if (rows >= 1)
            userName = resultSet.getString(ind);
        return userName;

    }

    public ArrayList<Score> getHighScores()
    {
        Log.d("scores", "getting scores");
        //int max = 10;
        //int num_entries = 0;
        ArrayList<Score> scores = new ArrayList<Score>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resultSet = db.rawQuery("Select * From " + TABLE_SCORES, null);
        resultSet.moveToFirst();
        while((!resultSet.isAfterLast()))// && (num_entries < max))
        {
            Score s = new Score(resultSet.getString(resultSet.getColumnIndex(COLUMN_USERNAME)),resultSet.getInt(resultSet.getColumnIndex(COLUMN_SCORE)));
            scores.add(s);
            Log.d("scores", String.valueOf(s.score));
            resultSet.moveToNext();
            //num_entries++;
        }

        System.out.println("done getting scores");

        return scores;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RoamGameSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

}

