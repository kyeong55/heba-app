package com.example.my8;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by JAEMYUNG on 15. 12. 8..
 */
@ParseClassName("UserInfo")
public class UserInfo extends ParseObject {
    public static final String CLASSNAME = "UserInfo";
    public static final String NAME = "name";
    public static final String PROFILE = "profile";
    public static final String WISHLIST = "wishlist";
    public static final String DONELIST = "donelist";
    public static final String ID = "userId";

    public UserInfo() {}

    public UserInfo(String username) {
        put(NAME, username);
    }

    public void setUserId(String userId) {
        put(ID, userId);
    }

    public String getUserId() {
        return getString(ID);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setProfile(ParseFile profile) {
        put(PROFILE, profile);
    }

    public ParseFile getProfile() {
        return getParseFile(PROFILE);
    }

    public List<String> getWishlist() {
        return getList(WISHLIST);
    }

    public List<String> getDonelist() {
        return getList(DONELIST);
    }

    public void addDonelist(String eventId) {
        addUnique(DONELIST, eventId);
    }

    public void addWishlist(String eventId) {
        addUnique(WISHLIST, eventId);
    }

    public static ParseQuery<UserInfo> getQuery() { return ParseQuery.getQuery(UserInfo.class); }
}
