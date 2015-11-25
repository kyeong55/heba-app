package com.example.my8;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_Event extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);

        EditText titleEditText = (EditText)findViewById(R.id.event_title);
        EditText commentEditText = (EditText)findViewById(R.id.stamp_comment);

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

        //stamp photo
        Bitmap stampPhoto = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        Bitmap stampPhotoScaled = Bitmap.createScaledBitmap(stampPhoto, 200, 200
                * stampPhoto.getHeight() / stampPhoto.getWidth(), false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        stampPhotoScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] scaledPhoto = bos.toByteArray();
        ParseFile stampPhotoFile = new ParseFile("meal_photo.jpg", scaledPhoto);

        //stamp comment
        String stampComment = commentEditText.getText().toString().trim();

        Stamp stamp = new Stamp();
        stamp.setLocation(stampLocation);
        stamp.setDatetime(stampDatetime);
        stamp.setPhotoFile(stampPhotoFile);
        stamp.setComment(stampComment);
        stamp.setUser(ParseUser.getCurrentUser());

        titleEditText.setHint("이벤트 제목을 입력해주세요.");
        String eventTitle = titleEditText.getText().toString().trim();



        //View stamp photo
        ImageView stampImageView = (ImageView)findViewById(R.id.stamp_photo);
        stampImageView.setImageBitmap(stampPhotoScaled);

        //Button for upload
        Button uploadButton = (Button)findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //todo upload bmp and event_title.
                SelectEvent selectEventActivity = (SelectEvent) SelectEvent.select_event_Activity;
                selectEventActivity.finish();
                finish();
            }
        });
    }
}