package com.example.my8;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by yjchang on 12/7/15.
 */
@ParseClassName("Friend")
public class Friend extends ParseObject {
    public static final String CLASSNAME = "Friend";
    public static final String FROM_USER = "from_user";
    public static final String TO_USER = "to_user";
    public static final String STATE = "state";

    enum State { REQUESTED, REJECTED, APPROVED }

    public ParseUser getFromUser() { return getParseUser(FROM_USER); }

    public void setFromUser(ParseUser user) { put(FROM_USER, user); }

    public ParseUser getToUser() { return getParseUser(TO_USER); }

    public void setToUser(ParseUser user) { put(TO_USER, user); }

    public State getState() { return State.valueOf(getString(STATE)); }

    public void setState(State state) { put(STATE, state.toString()); }

    public static ParseQuery<Friend> getQuery() { return ParseQuery.getQuery(Friend.class); }
}
