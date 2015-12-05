package com.example.my8;

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
    public Event() {
        // A default constructor is required.
    }

    public static final String CLASSNAME = "Event";
    public static final String TITLE = "title";

    public String getTitle() { return getString(TITLE); }

    public void setTitle(String title) { put(TITLE, title); }

    public int getNParticipant() {
        return getInt("nParticipant");
    }

    public void setNParticipant(int nParticipant) {
        put("nParticipant", nParticipant);
    }

    public ParseFile getThumbnail(int idx) {
        switch (idx) {
            case 1:
                return getParseFile("thumbnail1");
            case 2:
                return getParseFile("thumbnail2");
            case 3:
                return getParseFile("thumbnail3");
            default:
                return null;
        }
    }

    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }
}
