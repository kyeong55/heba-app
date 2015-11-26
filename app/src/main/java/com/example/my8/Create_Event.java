package com.example.my8;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_Event extends AppCompatActivity {
    private String stampComment;
    private Bitmap stampPhoto;
    private String eventTitle;
    private Bitmap stampPhotoScaled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);

        final EditText titleEditText = (EditText)findViewById(R.id.event_title);
        final EditText commentEditText = (EditText)findViewById(R.id.stamp_comment);

        //view stamp photo
        ImageView stampImageView = (ImageView)findViewById(R.id.stamp_photo);
        stampPhoto = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        stampPhotoScaled = Bitmap.createScaledBitmap(stampPhoto, 500, 500
                * stampPhoto.getHeight() / stampPhoto.getWidth(), false);
        stampImageView.setImageBitmap(stampPhotoScaled);

        titleEditText.setHint("이벤트 제목을 입력해주세요.");

        //button for upload
        Button uploadButton = (Button)findViewById(R.id.upload_button);
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
                } catch(ParseException e) {
                    e.printStackTrace();
                }

                //stamp comment
                stampComment = commentEditText.getText().toString();
                Log.w("debug!!!!!", "stampComment: " + stampComment);

                //stamp photo
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                stampPhotoScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] scaledPhoto = bos.toByteArray();
                ParseFile stampPhotoFile = new ParseFile("stamp.jpg", scaledPhoto);
                stampPhotoFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e != null) {
                            Log.w("debug:File", e+"");
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

                stamp.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e != null) {
                            Log.w("debug:stamp", e+"");
                        }
                    }
                });

                //event
                Event event = new Event();
                eventTitle = titleEditText.getText().toString();
                event.setTitle(eventTitle);

                //stamp-event relation
                //stamp.setEvent(event);

                //save
                event.saveInBackground();

                SelectEvent selectEventActivity = (SelectEvent) SelectEvent.select_event_Activity;
                finish();
                selectEventActivity.finish();

            }
        });
    }
}