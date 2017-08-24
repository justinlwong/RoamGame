/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.mygdx.roamgame.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import static com.mygdx.roamgame.backend.OfyService.ofy;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.roamgame.mygdx.com",
    ownerName = "backend.roamgame.mygdx.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Welcome back! Backend online.");

        //GameInfoData gInfo = new GameInfoData();

        //gInfo.score = 5l;
        //gInfo.user = name;

        // save to datastore
        //ofy().save().entity(gInfo).now();

        return response;
    }

    @ApiMethod(name = "uploadGameData")
    public void uploadGameData(@Named("gameID") int gameID, @Named("name") String name, @Named("score") int score, @Named("gameDuration") int gameDuration, @Named("inputFrequency") int inputFrequency, @Named("timestamp") String timestamp, @Named("isDemo") int isDemo) {
        //MyBean response = new MyBean();

        GameInfoData gInfo = new GameInfoData((long)gameID, name, (long)score, (long)gameDuration, inputFrequency, timestamp, isDemo);
        // save to datastore
        ofy().save().entity(gInfo).now();

        //return response;
    }

    @ApiMethod(name = "uploadEventData")
    public void uploadEventData(@Named("gameID") int gameID, @Named("userName") String userName, @Named("timestamp") int timeStamp, @Named("levelNo") int levelNo, @Named("evenType") int evenType ,@Named("currentHealth") int currentHealth, @Named("currentScore") int currentScore, @Named("currentLevelScore") int currentLevelScore, @Named("barrelDecision") int barrelDecision, @Named("hazardType") int hazardType, @Named("distanceFromExit") int distanceFromExit, @Named("levelDuration") int levelDuration, @Named("closestZombieDistance") int closestZombieDistance, @Named("exitDistance") int exitDistance,  @Named("currentStreak") int currentStreak,  @Named("currentTimer") int currentTimer,  @Named("abilityType") int abilityType,  @Named("abilityActive") int abilityActive)  {
        //MyBean response = new MyBean();

        EventInfoData eInfo = new EventInfoData((long)gameID, userName, (long)timeStamp, levelNo, evenType, (long)currentHealth, (long)currentScore, (long)currentLevelScore, barrelDecision, hazardType, distanceFromExit, levelDuration, closestZombieDistance, exitDistance, currentStreak, currentTimer, abilityType, abilityActive);
        // save to datastore
        ofy().save().entity(eInfo).now();



        //return response;
    }

    @ApiMethod(name = "getScores")
    public void getScores(@Named("userName") String userName)
    {
        Query<GameInfoData> gInfo = ofy().load().type(GameInfoData.class).filter("user", userName);

        //String res = gInfo.first().toString();

        //return res;



    }

    @ApiMethod(name ="verifyID")
    public MyBean verifyID(@Named("checkID") int checkID)
    {
        Query<RegisteredUserIDData> rInfo = ofy().load().type(RegisteredUserIDData.class).filter("userID", checkID);

        RegisteredUserIDData res = rInfo.first().now();
        MyBean response = new MyBean();
        if (res!= null)
        {
            response.setUserID(res.getUserID());
            response.setData("Verified ID! You can now enter the game!");
        } else
        {
            response.setData("The submitted ID was incorrect! Please try again!");
            response.setUserID(-1);
        }
        return response;

    }

    @ApiMethod(name = "registerID")
    public void registerID(@Named("registeredID")int registeredID)
    {
        RegisteredUserIDData rID = new RegisteredUserIDData();
        rID.setUserID(registeredID);
        ofy().save().entity(rID).now();
    }

    @ApiMethod(name = "createIDGenerator")
    public void createIDGenerator(@Named("initialVal")int initialVal)
    {
        GameIDData gInfo = new GameIDData();

        gInfo.setID(initialVal);

        ofy().save().entity(gInfo).now();
    }

    @ApiMethod(name = "getNewID")
    public MyBean getNewID()
    {
        Query<GameIDData> gInfo = ofy().load().type(GameIDData.class);

        GameIDData entry = gInfo.first().now();
        int newID = entry.getID();

        MyBean response = new MyBean();
        response.setIDData(newID);

        // update
        entry.incrementID();
        ofy().save().entity(entry).now();

        return response;
    }

}
