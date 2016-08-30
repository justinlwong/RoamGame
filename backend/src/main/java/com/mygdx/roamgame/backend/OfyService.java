package com.mygdx.roamgame.backend;

/**
 * Created by Justin on 2016-08-30.
 */
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {
    static {
        ObjectifyService.register(GameInfoData.class);
    }
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
