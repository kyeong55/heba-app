package com.example.my8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("이벤트 정보");

        final TextView titleTextView = (TextView) findViewById(R.id.event_info_title);
        final TextView stampCountTextView = (TextView) findViewById(R.id.event_info_stamp_count);
        final TextView timeTextView = (TextView) findViewById(R.id.event_info_time);
        final TextView locationTextView = (TextView) findViewById(R.id.event_info_location);
        final ParseImageView imageView1 = (ParseImageView) findViewById(R.id.event_info_image1);
        final ParseImageView imageView2 = (ParseImageView) findViewById(R.id.event_info_image2);
        final ParseImageView imageView3 = (ParseImageView) findViewById(R.id.event_info_image3);
        final TextView seeMoreButton = (TextView) findViewById(R.id.event_info_see_more);

        View map_layout = findViewById(R.id.event_info_map_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) map_layout.getLayoutParams();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        params.height = metrics.heightPixels/2;
        map_layout.setLayoutParams(params);

        String eventId = getIntent().getStringExtra("event_id");
        ParseQuery<Event> query = Event.getQuery();
        query.getInBackground(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                titleTextView.setText(event.getTitle());
                stampCountTextView.setText(event.getNParticipant()+"");
                //TODO: event 대표사진(3개 미만일 경우 visibility = NONE), 시간, 위치
                //TODO: 더보기에 스탬프들 볼 수 있는 listener
//                imageView.setParseFile(stamp.getPhotoFile());
//                imageView.loadInBackground();
//                textView.setText(stamp.getComment());
//                progressBar.setVisibility(View.GONE);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.event_info_map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        // Add a marker in Sydney and move the camera
        //LatLng loc = new LatLng(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude")));
        LatLng loc = new LatLng(30,30);
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in picture"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }
}
