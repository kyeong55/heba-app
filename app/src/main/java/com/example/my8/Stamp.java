package com.example.my8;

import com.google.android.gms.vision.barcode.Barcode;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
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

    public Date getDatetime() {
        return getDate("datetime");
    }

    public void setDatetime(Date datetime) {
        put("datetime", datetime);
    }

    public String getComment() {
        return getString("comment");
    }

    public void setComment(String comment) {
        put("comment", comment);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
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
        return getParseFile("photo");
    }

    public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

    public ParseObject getEvent() {
        return getParseObject("event");
    }

    public void setEvent(Event event) {
        put("event", event);
    }
}
