package com.example.my8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends AppCompatActivity {
    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_activity_fade_in, R.anim.trans_activity_none);
        } else {
            // Start and intent for the logged out activity
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_activity_fade_in, R.anim.trans_activity_none);
        }
    }
}
