package com.example.my8;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Create_Event extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);
        TextView t = (TextView)findViewById(R.id.content_position);
        Intent intent = getIntent();
        t.setText(intent.getStringExtra("position"));
        Bitmap bmp = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        ImageView i = (ImageView)findViewById(R.id.content_image);
        i.setImageBitmap(bmp);
    }
}
