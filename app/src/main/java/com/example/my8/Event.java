package com.example.my8;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Event
 */
@ParseClassName("Event")
public class Event extends ParseObject {
    public Event() {}

    private static final String CLASSNAME = "Event";
    public static final String TITLE = "title";
    public static final String PARTICIPANT = "nParticipant";
    private static final String THUMBNAIL = "thumbnail";
    private static final String STAMP = "stamp";
    private static final String INDEX = "thumbnailIndex";
    private static final String MEAN_LA = "xMean";
    private static final String MEAN_LO = "yMean";
    private static final String VAR_LA = "xVar";
    private static final String VAR_LO = "yVar";
    public static final String DATETIME = "datetime";

    public Event(String title, Date datetime) {
        put(TITLE, title);
        put(DATETIME, datetime);
        put(PARTICIPANT, 0);
        put(INDEX, 0);
        put(MEAN_LA, 0.0);
        put(MEAN_LO, 0.0);
        put(VAR_LA, 0.0);
        put(VAR_LO, 0.0);
    }

    public Double getRadius() {
        Double x = 111*1000*Math.sqrt(getVarLa());
        Double y = 88.8*1000*Math.sqrt(getVarLo());
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    public Double getMeanLa() {
        return getDouble(MEAN_LA);
    }

    public Double getMeanLo() {
        return getDouble(MEAN_LO);
    }

    public Double getVarLa() {
        return getDouble(VAR_LA);
    }

    public Double getVarLo() {
        return getDouble(VAR_LO);
    }

    public String getTitle() { return getString(TITLE); }

    public int getNParticipant() {
        return getInt(PARTICIPANT);
    }

    private int getIndex() {
        return getInt(INDEX);
    }

    public ParseFile getThumbnail(int idx) {
        return getParseFile(THUMBNAIL + ((getIndex() - idx + 6) % 6));
    }

    public Stamp getStamp(int idx) {
        return (Stamp)getParseObject(STAMP + ((getIndex() - idx + 6) % 6));
    }

    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }
}
