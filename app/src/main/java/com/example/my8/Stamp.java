package com.example.my8;

import com.google.android.gms.vision.barcode.Barcode;
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
 * about a given Stamp
 */
@ParseClassName("Stamp")
public class Stamp extends ParseObject {
    public Stamp() {
        // A default constructor is required.
    }

    public static final String CLASSNAME = "Stamp";
    public static final String DATETIME = "datetime";
    public static final String COMMENT = "comment";
    public static final String USER = "user";
    public static final String LOCATION = "location";
    public static final String PHOTO = "photo";
    public static final String EVENT = "event";

    public Date getDatetime() {
        return getDate(DATETIME);
    }

    public void setDatetime(Date datetime) {
        put(DATETIME, datetime);
    }

    public String getComment() {
        return getString(COMMENT);
    }

    public void setComment(String comment) {
        put(COMMENT, comment);
    }

    public ParseUser getUser() {
        return getParseUser(USER);
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION);
    }

    public void setLocation(ParseGeoPoint location) {
        put(LOCATION, location);
    }
/*
    public String getRating() {
        return getString("rating");
    }

    public void setRating(String rating) {
        put("rating", rating);
    }
*/
    public ParseFile getPhotoFile() {
        return getParseFile(PHOTO);
    }

    public void setPhotoFile(ParseFile file) {
        put(PHOTO, file);
    }

    public ParseObject getEvent() {
        return getParseObject(EVENT);
    }

    public void setEvent(Event event) {
        put(EVENT, event);
    }

    public static ParseQuery<Stamp> getQuery() {
        return ParseQuery.getQuery(Stamp.class);
    }
}
