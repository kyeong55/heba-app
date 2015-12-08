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

    public static final String LOCATION = "location";
    public static final String DATETIME = "datetime";
    public static final String COMMENT = "comment";
    public static final String THUMBNAIL = "thumbnail";
    public static final String PHOTO = "photo";
    public static final String USERINFO = "userInfo";
    public static final String TITLE = "eventTitle";
    public static final String ID = "eventId";

    public Stamp(ParseGeoPoint location, Date datetime, String comment, ParseFile thumbnail,
                 ParseFile photo, UserInfo userInfo, String eventTitle, String eventId) {
        put(LOCATION, location);
        put(DATETIME, datetime);
        put(COMMENT, comment);
        put(THUMBNAIL, thumbnail);
        put(PHOTO, photo);
        put(USERINFO, userInfo);
        put(TITLE, eventTitle);
        put(ID, eventId);
    }

    public Date getDatetime() {
        return getDate(DATETIME);
    }

    public String getComment() {
        return getString(COMMENT);
    }

    public UserInfo getUserInfo() {
        return (UserInfo)getParseObject(USERINFO);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(LOCATION);
    }

    public ParseFile getPhotoFile() {
        return getParseFile(PHOTO);
    }

    public ParseFile getThumbnail() {
        return getParseFile(THUMBNAIL);
    }

    public String getEventId() {
        return getString(ID);
    }

    public static ParseQuery<Stamp> getQuery() {
        return ParseQuery.getQuery(Stamp.class);
    }
}
