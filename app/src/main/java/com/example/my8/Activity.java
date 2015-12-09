package com.example.my8;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by JAEMYUNG on 15. 12. 7..
 */
@ParseClassName("Activity")
public class Activity extends ParseObject {
    public static final String CLASSNAME = "Activity";
    public static final String USER = "user";
    public static final String ACTION = "action";
    public static final String EVENT = "object";

    public static final int WISHLIST = 0;
    public static final int STAMP = 1;

    public Activity() {}

    public Activity(ParseUser user, int action, Event event) {
        put(USER, user);
        put(ACTION, action);
        put(EVENT, event);
    }

    public ParseUser getUser() {
        return getParseUser(USER);
    }

    public int getAction() {
        return getInt(ACTION);
    }

    public Event getEvent() {
        return (Event)getParseObject(EVENT);
    }

    public String discription() {
        String user = getParseUser(USER).getUsername();
        String action = "";
        switch (getInt(ACTION)) {
            case 0:
                action = "위시리스트에 추가하였습니다.";
                break;
            case 1:
                action = "참여했습니다.";
                break;
        }
        return user + "님이 " + action;
    }

    public static ParseQuery<Activity> getQuery() { return ParseQuery.getQuery(Activity.class); }
}
