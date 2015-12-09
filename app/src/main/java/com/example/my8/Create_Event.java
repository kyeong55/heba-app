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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
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
    private String isSelectEvent;

    private ExifInterface exif;

    EditText titleEditText;
    EditText commentEditText;

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
        isSelectEvent = getIntent().getStringExtra("isSelectActivity");
        if (eventId == null) {
            titleEditText.setHint("이벤트 제목을 입력해주세요.");
        } else {
            eventTitle = getIntent().getStringExtra("eventTitle");
            titleEditText.setText(eventTitle);
            titleEditText.setKeyListener(null);
            commentEditText.requestFocus();
        }

        //stamp location
        float geopoint[] = new float[2];
        exif.getLatLong(geopoint);
        String latitude = geopoint[0] + "";
        String longitude = geopoint[1] + "";
        stampLocation = new ParseGeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));

        //stamp datetime
        String datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
        //SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                //(SimpleDateFormat) android.text.format.DateFormat.getDateFormat(getApplicationContext());
        stampDatetime = null;
        try {
            Log.d("hehehe", "exif: " + exif.toString());
            Log.d("hehehe", "DATE_TIME: " + exif.getAttribute(ExifInterface.TAG_DATETIME));
            Log.d("hehehe", "DATE_TIME_DIGITIZED: " + exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED));
            Log.d("hehehe", "datetime: " + datetime);
            Log.d("hehehe", "dateformat: " + dateFormat.toPattern());
            stampDatetime = dateFormat.parse(datetime);
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
                        if (e == null) {
                            eventId = event.getObjectId();

                            ParseUser.getCurrentUser().addUnique(User.DONELIST, eventId);
                            ParseUser.getCurrentUser().saveInBackground();

                            ActionContract actionContract = new ActionContract(ParseUser.getCurrentUser(), ActionContract.STAMP, event);
                            actionContract.saveInBackground();

                            Stamp stamp = new Stamp(stampLocation, stampDatetime, stampComment, scaledStampPhotoFile,
                                    stampPhotoFile, ParseUser.getCurrentUser(), eventTitle, eventId);

                            stamp.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(com.parse.ParseException e) {
                                    if (e == null) {
                                        dialog.dismiss();

                                        //ActionContract change
                                        if (isSelectEvent.equalsIgnoreCase("true")) {
                                            SelectEvent selectEventActivity = (SelectEvent) SelectEvent.selectEventActivity;
                                            selectEventActivity.finish();
                                        } else {
                                            EventInfoActivity eventInfoActivity = (EventInfoActivity) EventInfoActivity.eventInfoActivity;
                                            eventInfoActivity.refresh();
                                        }
                                        MainActivity mainActivity = (MainActivity) MainActivity.mainActivity;
                                        mainActivity.refreshAll();
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(Create_Event.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.d("shit!!!!", e.getMessage());
                        }
                    }
                });
            } else {
                ParseUser.getCurrentUser().addUnique(User.DONELIST, eventId);
                ParseUser.getCurrentUser().saveInBackground();

                ParseQuery<Event> query = Event.getQuery();
                query.getInBackground(eventId, new GetCallback<Event>() {
                    @Override
                    public void done(Event event, com.parse.ParseException e) {
                        ActionContract actionContract = new ActionContract(ParseUser.getCurrentUser(), ActionContract.STAMP, event);
                        actionContract.saveInBackground();
                    }
                });

                Stamp stamp = new Stamp(stampLocation, stampDatetime, stampComment, scaledStampPhotoFile,
                        stampPhotoFile, ParseUser.getCurrentUser(), eventTitle, eventId);

                stamp.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e == null) {
                            dialog.dismiss();

                            //ActionContract change
                            if (isSelectEvent.equalsIgnoreCase("true")) {
                                SelectEvent selectEventActivity = (SelectEvent) SelectEvent.selectEventActivity;
                                selectEventActivity.finish();
                            }
                            else {
                                EventInfoActivity eventInfoActivity = (EventInfoActivity) EventInfoActivity.eventInfoActivity;
                                eventInfoActivity.refresh();
                            }
                            finish();
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