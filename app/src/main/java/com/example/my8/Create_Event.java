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
import android.util.Log;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_Event extends AppCompatActivity {
    private String stampComment;
    private Bitmap stampPhoto;
    private String eventTitle;
    private Bitmap rotatedScaledStampPhoto;
    private Bitmap stampPhotoScaled;
    private ExifInterface exif;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);

        final EditText titleEditText = (EditText) findViewById(R.id.event_title);
        final EditText commentEditText = (EditText) findViewById(R.id.stamp_comment);

        //view stamp photo
        String imgPath = getIntent().getStringExtra("image_path");
        ImageView stampImageView = (ImageView) findViewById(R.id.stamp_photo);
        stampPhoto = BitmapFactory.decodeFile(imgPath);
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        titleEditText.setHint("이벤트 제목을 입력해주세요.");

        //button for upload
        Button uploadButton = (Button) findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();

                //stamp location
                ParseGeoPoint stampLocation = new ParseGeoPoint(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude")));

                //stamp datetime
                SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date stampDatetime = null;
                try {
                    stampDatetime = format.parse(intent.getStringExtra("datetime"));
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
                Event event = new Event();
                eventTitle = titleEditText.getText().toString();
                event.setTitle(eventTitle);
                event.setNParticipant(1);

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

                SelectEvent selectEventActivity = (SelectEvent) SelectEvent.select_event_Activity;
                finish();
                selectEventActivity.finish();

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
}