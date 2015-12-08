package com.example.my8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final int REQ_CODE_SELECT_STAMP_IMAGE = 200;

    private String eventId;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("이벤트 정보");

        View mapLayout = findViewById(R.id.event_info_map_layout);
        final TextView titleTextView = (TextView) findViewById(R.id.event_info_title);
        final TextView stampCountTextView = (TextView) findViewById(R.id.event_info_stamp_count);
        final TextView timeTextView = (TextView) findViewById(R.id.event_info_time);
        final TextView locationTextView = (TextView) findViewById(R.id.event_info_location);
        final ParseImageView imageView1 = (ParseImageView) findViewById(R.id.event_info_image1);
        final ParseImageView imageView2 = (ParseImageView) findViewById(R.id.event_info_image2);
        final ParseImageView imageView3 = (ParseImageView) findViewById(R.id.event_info_image3);
        final ParseImageView imageView4 = (ParseImageView) findViewById(R.id.event_info_image4);
        final View imageLayout = findViewById(R.id.event_info_image_layout);
        final TextView seeMoreButton = (TextView) findViewById(R.id.event_info_see_more);

        final DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);


        LinearLayout.LayoutParams mapParams = (LinearLayout.LayoutParams) mapLayout.getLayoutParams();
        if (metrics.heightPixels > metrics.widthPixels)
            mapParams.height = metrics.heightPixels/3;
        else
            mapParams.height = metrics.heightPixels/2;
        mapLayout.setLayoutParams(mapParams);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.event_info_fab);

        eventId = getIntent().getStringExtra("event_id");
        ParseQuery<Event> query = Event.getQuery();
        query.getInBackground(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                int numOfPhoto = (int)((float)metrics.widthPixels/metrics.xdpi/1.4);
                if (numOfPhoto < 2)
                    numOfPhoto = 2;
                else if (numOfPhoto > 4)
                    numOfPhoto = 4;
                eventTitle = event.getTitle();
                titleTextView.setText(eventTitle);
                stampCountTextView.setText(event.getNParticipant() + "");
                ParseFile thumbnail1 = event.getThumbnail(1);
                ParseFile thumbnail2 = event.getThumbnail(2);
                ParseFile thumbnail3 = event.getThumbnail(3);
                ParseFile thumbnail4 = event.getThumbnail(4);
                if (thumbnail1 != null) {
                    imageView1.setVisibility(View.VISIBLE);
                    imageView1.setParseFile(thumbnail1);
                    imageView1.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {}
                    });
                }
                if (thumbnail2 != null) {
                    imageView2.setVisibility(View.VISIBLE);
                    imageView2.setParseFile(thumbnail2);
                    imageView2.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {}
                    });
                }
                else
                    numOfPhoto = 1;
                if ((thumbnail3 != null) && (numOfPhoto >= 3)) {
                    imageView3.setVisibility(View.VISIBLE);
                    imageView3.setParseFile(thumbnail3);
                    imageView3.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {}
                    });
                }
                else if (numOfPhoto >= 3)
                    numOfPhoto = 2;
                if ((thumbnail4 != null) && (numOfPhoto == 4)) {
                    imageView4.setVisibility(View.VISIBLE);
                    imageView4.setParseFile(thumbnail4);
                    imageView4.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {}
                    });
                }
                else if (numOfPhoto == 4)
                    numOfPhoto = 3;

                LinearLayout.LayoutParams imageParams = (LinearLayout.LayoutParams) imageLayout.getLayoutParams();
                imageParams.height = metrics.widthPixels/numOfPhoto;
                imageLayout.setLayoutParams(imageParams);

                //TODO: event 대표시간, 위치
                //TODO: 더보기에 스탬프들 볼 수 있는 listener

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQ_CODE_SELECT_STAMP_IMAGE);
                    }
                });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_wishlist) {
            List<String> wishlist = ParseUser.getCurrentUser().getList("wishlist");
            List<String> donelist = ParseUser.getCurrentUser().getList("donelist");
            if (donelist != null && donelist.contains(eventId)) {
                Toast.makeText(getApplicationContext(), "이미 활동을 완료하였습니다", Toast.LENGTH_SHORT).show();
            } else if (wishlist != null && wishlist.contains(eventId)) {
                Toast.makeText(getApplicationContext(), "이미 위시리스트에 추가되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "위시리스트에 추가되었습니다", Toast.LENGTH_SHORT).show();
                ParseUser.getCurrentUser().addUnique("wishlist", eventId);
                ParseUser.getCurrentUser().saveInBackground();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_SELECT_STAMP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePath = getImageNameToUri(data.getData());

                Intent toCreateEventActivity = new Intent(this, Create_Event.class);
                toCreateEventActivity.putExtra("imagePath", imagePath);
                toCreateEventActivity.putExtra("eventId", eventId);
                toCreateEventActivity.putExtra("eventTitle", eventTitle);
                toCreateEventActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toCreateEventActivity);
                overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
            }
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }
}
