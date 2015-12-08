package com.example.my8;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_Event extends AppCompatActivity {
    private Bitmap rotatedStampPhoto;
    private Bitmap scaledRotatedStampPhoto;
    private Bitmap stampPhoto;

    private ParseGeoPoint stampLocation;
    private Date stampDatetime;
    private String stampComment;
    private ParseFile scaledStampPhotoFile;
    private ParseFile stampPhotoFile;

    private String eventId;
    private String eventTitle;

    private ExifInterface exif;

    EditText titleEditText;
    EditText commentEditText;

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

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        rotatedStampPhoto = Bitmap.createBitmap(stampPhoto, 0,
                0, stampPhoto.getWidth(), stampPhoto.getHeight(),
                matrix, true);

        stampImageView.setImageBitmap(rotatedStampPhoto);

        if (rotatedStampPhoto.getWidth() > rotatedStampPhoto.getHeight()) {
            if (rotatedStampPhoto.getHeight() > 500) {
                scaledRotatedStampPhoto = Bitmap.createScaledBitmap(rotatedStampPhoto,
                        500 * rotatedStampPhoto.getWidth() / rotatedStampPhoto.getHeight(), 500, false);
            } else {
                scaledRotatedStampPhoto = rotatedStampPhoto;
            }
        } else {
            if (rotatedStampPhoto.getWidth() > 500) {
                scaledRotatedStampPhoto = Bitmap.createScaledBitmap(rotatedStampPhoto, 500, 500
                        * rotatedStampPhoto.getHeight() / rotatedStampPhoto.getWidth(), false);
            } else {
                scaledRotatedStampPhoto = rotatedStampPhoto;
            }
        }

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            titleEditText.setHint("이벤트 제목을 입력해주세요.");
        } else {
            eventTitle = getIntent().getStringExtra("eventTitle");
            titleEditText.setText(eventTitle);
            titleEditText.setKeyListener(null);
            commentEditText.requestFocus();
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //stamp location
        float geopoint[] = new float[2];
        exif.getLatLong(geopoint);
        String latitude = geopoint[0] + "";
        String longitude = geopoint[1] + "";
        stampLocation = new ParseGeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));

        //stamp datetime
        String datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        stampDatetime = null;
        try {
            stampDatetime = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            final ProgressDialog dialog = new ProgressDialog(Create_Event.this);
            dialog.setMessage("업로드 중");
            dialog.show();

            //stamp comment
            stampComment = commentEditText.getText().toString();

            //stamp photo
            ByteArrayOutputStream scaledBos = new ByteArrayOutputStream();
            scaledRotatedStampPhoto.compress(Bitmap.CompressFormat.JPEG, 50, scaledBos);
            byte[] scaledPhoto = scaledBos.toByteArray();
            scaledStampPhotoFile = new ParseFile("thumbnail.jpg", scaledPhoto);
            scaledStampPhotoFile.saveInBackground();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            rotatedStampPhoto.compress(Bitmap.CompressFormat.JPEG, 25, bos);
            byte[] Photo = bos.toByteArray();
            stampPhotoFile = new ParseFile("original.jpg", Photo);
            stampPhotoFile.saveInBackground();

            //event
            if (eventId == null) {
                eventTitle = titleEditText.getText().toString();
                final Event event = new Event(eventTitle);
                ParseACL eventACL = new ParseACL();
                eventACL.setPublicReadAccess(true);
                eventACL.setPublicWriteAccess(true);
                event.setACL(eventACL);
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        eventId = event.getObjectId();

                        UserInfo userInfo = (UserInfo)ParseUser.getCurrentUser().getParseObject("userInfo");
                        userInfo.addDonelist(eventId);
                        userInfo.saveInBackground();

                        Stamp stamp = new Stamp(stampLocation, stampDatetime, stampComment, scaledStampPhotoFile,
                                stampPhotoFile, userInfo, eventTitle, eventId);

                        stamp.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    dialog.dismiss();

                                    //Activity change
                                    SelectEvent selectEventActivity = (SelectEvent) SelectEvent.selectEventActivity;
                                    finish();
                                    selectEventActivity.finish();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(Create_Event.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            } else {
                UserInfo userInfo = (UserInfo)ParseUser.getCurrentUser().getParseObject("userInfo");
                userInfo.addDonelist(eventId);
                userInfo.saveInBackground();

                Stamp stamp = new Stamp(stampLocation, stampDatetime, stampComment, scaledStampPhotoFile,
                        stampPhotoFile, userInfo, eventTitle, eventId);

                stamp.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            dialog.dismiss();

                            //Activity change
                            SelectEvent selectEventActivity = (SelectEvent) SelectEvent.selectEventActivity;
                            finish();
                            selectEventActivity.finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(Create_Event.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }
}