package com.example.my8;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Create_Event extends AppCompatActivity {
    String event_title;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__event);
        Button button = (Button)findViewById(R.id.upload);
        EditText t = (EditText)findViewById(R.id.content_position);
        Intent intent = getIntent();
        t.setText(intent.getStringExtra("position"));
        event_title = t.getText().toString();
        bmp = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        ImageView i = (ImageView)findViewById(R.id.content_image);
        i.setImageBitmap(bmp);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //todo upload bmp and event_title.
                SelectEvent selectevent = (SelectEvent) SelectEvent.select_event_Activity;
                finish();
                selectevent.finish();
            }
        });
    }
}