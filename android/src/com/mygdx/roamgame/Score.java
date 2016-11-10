package com.mygdx.roamgame;

/**
 * Created by Justin on 2016-11-01.
 */
public class Score implements Comparable{

    public String name;
    public Integer score;

    public Score(String n, Integer s)
    {
        name = n;
        score = s;
    }


    @Override
    public int compareTo(Object s) {
        int compareScore=((Score)s).score;
        return compareScore - this.score;
    }

}
