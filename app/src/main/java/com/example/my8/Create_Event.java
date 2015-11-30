package com.example.my8;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Create_Event extends AppCompatActivity {
    private String stampComment;
    private Bitmap stampPhoto;
    private String eventTitle;
    private Bitmap rotatedScaledStampPhoto;
    private Bitmap stampPhotoScaled;
    private ExifInterface exif;
    private String eventId;
    private Event event;

    EditText titleEditText;
    EditText commentEditText;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("스탬프 찍기");

        titleEditText = (EditText) findViewById(R.id.event_title);
        commentEditText = (EditText) findViewById(R.id.stamp_comment);

        //view stamp photo
        String imagePath = getIntent().getStringExtra("imagePath");
        exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView stampImageView = (ImageView) findViewById(R.id.stamp_photo);
        stampPhoto = BitmapFactory.decodeFile(imagePath);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        stampPhotoScaled = Bitmap.createScaledBitmap(stampPhoto, 500, 500
                * stampPhoto.getHeight() / stampPhoto.getWidth(), false);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        rotatedScaledStampPhoto = Bitmap.createBitmap(stampPhotoScaled, 0,
                0, stampPhotoScaled.getWidth(), stampPhotoScaled.getHeight(),
                matrix, true);
        stampImageView.setImageBitmap(rotatedScaledStampPhoto);

        event = null;
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            titleEditText.setHint("이벤트 제목을 입력해주세요.");
        } else {
            ParseQuery<Event> query = Event.getQuery();
            try {
                event = query.get(eventId);
                titleEditText.setText(event.getTitle());
                titleEditText.setKeyListener(null);
                commentEditText.requestFocus();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Create_Event Page", // TODO: Define a title for the content shown.
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
                "Create_Event Page", // TODO: Define a title for the content shown.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_commit) {
            float geopoint[] = new float[2];
            exif.getLatLong(geopoint);

            String latitude = geopoint[0] + "";
            String longitude = geopoint[1] + "";
            String datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);

            //stamp location
            ParseGeoPoint stampLocation = new ParseGeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));

            //stamp datetime
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            Date stampDatetime = null;
            try {
                stampDatetime = format.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //stamp comment
            stampComment = commentEditText.getText().toString();
            Log.w("debug!!!!!", "stampComment: " + stampComment);

            //stamp photo
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            rotatedScaledStampPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] scaledPhoto = bos.toByteArray();
            ParseFile stampPhotoFile = new ParseFile("stamp.jpg", scaledPhoto);
            stampPhotoFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e != null) {
                        Log.w("debug:File", e + "");
                    }
                }
            });

            //stamp
            Stamp stamp = new Stamp();
            stamp.setLocation(stampLocation);
            stamp.setDatetime(stampDatetime);
            stamp.setPhotoFile(stampPhotoFile);
            stamp.setComment(stampComment);
            stamp.setUser(ParseUser.getCurrentUser());

            //event
            if (event == null) {
                Event event = new Event();
                eventTitle = titleEditText.getText().toString();
                event.setTitle(eventTitle);
                event.setNParticipant(1);
            } else {
                event.increment("nParticipant");
            }

            //event.setACL(postACL);

            //stamp-event relation
            stamp.setEvent(event);

            //save
            stamp.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e != null) {
                        Log.w("debug:stamp", e + "");
                    }
                }
            });

            event.saveInBackground();

            SelectEvent selectEventActivity = (SelectEvent) SelectEvent.selectEventActivity;
            finish();
            selectEventActivity.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}