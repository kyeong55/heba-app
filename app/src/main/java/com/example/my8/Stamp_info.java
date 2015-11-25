package com.example.my8;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class Stamp_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_info);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        StampAdapter stadapter = new StampAdapter(getApplicationContext());
        Intent intent = getIntent();
        int pos = intent.getIntExtra("currentstamppos", 0);
        int[] stampidlist = intent.getIntArrayExtra("stamplistid");
        pager.setAdapter(stadapter);
        for(int j = pos; j < stampidlist.length; j++) {
            stadapter.add(stampidlist[j], stampidlist[j] + "", null);
        }
        //todo get image_id  from main activity
        //Intent intent = getIntent();
    }
}
