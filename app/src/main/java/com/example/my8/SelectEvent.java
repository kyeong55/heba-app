package com.example.my8;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

//public class SelectEvent extends FragmentActivity implements OnMapReadyCallback {
public class SelectEvent extends FragmentActivity {
    private SelectEventImageAdapter selectEventImageAdapter;

    //private GoogleMap mMap;
    public static Activity selectEventActivity;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectEventActivity = SelectEvent.this;
        setContentView(R.layout.activity_select_event);

        RecyclerView selectEventView = (RecyclerView) findViewById(R.id.select_event_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(layoutManager.HORIZONTAL);
        selectEventView.setHasFixedSize(true);
        selectEventView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        selectEventImageAdapter = new SelectEventImageAdapter(getApplicationContext(), imagePath);
        selectEventView.setAdapter(selectEventImageAdapter);
        refresh();

        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void refresh() {
        ParseQuery<Event> query = Event.getQuery();
        Log.w("debugging", ParseUser.getCurrentUser().getList("wishlist") + "");
        query.whereContainedIn("objectId", ParseUser.getCurrentUser().getList("wishlist"));
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    List<Select_Event_Image_item> items = new ArrayList<>();
                    for (Event event : events) {
                        ParseQuery<Stamp> subQuery = Stamp.getQuery();
                        subQuery.whereEqualTo("event", event);
                        try {
                            Stamp stamp = subQuery.getFirst();
                            items.add(new Select_Event_Image_item(event, stamp));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    selectEventImageAdapter.setItems(items);
                    selectEventImageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectEvent Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.my8/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectEvent Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.my8/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        // Add a marker in Sydney and move the camera
        LatLng loc = new LatLng(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude")));
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in picture"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
    }
    */
}
