package com.example.my8;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Subclass for friend
 */
@ParseClassName("Friend")
public class Friend extends ParseObject {
    public static final String CLASSNAME = "Friend";
    public static final String FROM_USER = "fromUser";
    public static final String FROM_USER_ID = "fromUserId";
    public static final String TO_USER_ID = "toUserId";
    public static final String STATE = "state";

    public static final int REQUESTED = 0;
    public static final int REJECTED = 1;
    public static final int APPROVED = 2;

    public Friend() {}

    public Friend(UserInfo fromUserInfo, String fromUserId, String toUserId) {
        put(FROM_USER, fromUserInfo);
        put(FROM_USER_ID, fromUserId);
        put(TO_USER_ID, toUserId);
        put(STATE, REQUESTED);
    }

    public ParseObject getFromUser() {
        return getParseObject(FROM_USER);
    }

    public String getFromUserId() { return getString(FROM_USER_ID); }

    public String getToUserId() { return getString(TO_USER_ID); }

    public int getState() { return getInt(STATE); }

    public void setState(int state) { put(STATE, state); }

    public static ParseQuery<Friend> getQuery() { return ParseQuery.getQuery(Friend.class); }
}
