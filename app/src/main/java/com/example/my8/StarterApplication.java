package com.example.my8;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by yjchang on 11/25/15.
 */
public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /* Enable Local Datastore. */
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Stamp.class);
        Parse.initialize(this,
                "FxIKD0gxTd4rdcDyHxKZZG9XxeBMrKT1KmgM7xCl",
                "BDkifQnOh7JCDhp5Olbkr86daYT14O4yYlAj71sN");

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
