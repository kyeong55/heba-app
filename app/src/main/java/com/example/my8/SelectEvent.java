package com.example.my8;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectEvent extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static Activity select_event_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        select_event_Activity = SelectEvent.this;
        setContentView(R.layout.activity_select_event);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //TextView t = (TextView)findViewById(R.id.inform);
        //Bitmap bmp = BitmapFactory.decodeFile(getIntent().getStringExtra("imagename"));
        //ImageView i = (ImageView)findViewById(R.id.image_select);

        Intent intent = getIntent();
        //i.setImageBitmap(bmp);

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.select_event_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(layoutManager.HORIZONTAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        SelectEventImageAdapter sadapter = new SelectEventImageAdapter(getApplicationContext(), intent.getStringExtra("image_path"));

        recyclerView.setAdapter(sadapter);
        for(int j = 0; j < 4; j++) {
            sadapter.add(j, j+"", null);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //GoogleMap mGoogleMap;
        //LatLng loc = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude"))); // 위치 좌표 설정
        //CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(14).build();
        //MarkerOptions marker = new MarkerOptions().position(loc); // 구글맵에 기본마커 표시

        //mGoogleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
// 화면에 구글맵 표시
        //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // 지정위치로 이동
        //mGoogleMap.addMarker(marker); // 지정위치에 마커 추가
        //t.setText(intent.getStringExtra("latitude"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng loc = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude")));
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in picture"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
    }
}
