package com.example.my8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//public class SelectEvent extends FragmentActivity implements OnMapReadyCallback {
public class SelectEvent extends AppCompatActivity implements OnMapReadyCallback {
    private SelectEventImageAdapter selectEventImageAdapter;

    private GoogleMap mMap;
    private List<Marker> markers;
    private Marker newStampMarker;

    public static Activity selectEventActivity;
    private ViewPager selectViewPager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectEventActivity = SelectEvent.this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_select_event);

//        setRequestedOrientation();
//        Log.d("dddd", "get requested ori  "+getRequestedOrientation());
//        ActivityInfo

        Toolbar toolbar = (Toolbar) findViewById(R.id.select_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("이벤트 선택");

//        RecyclerView selectEventView = (RecyclerView) findViewById(R.id.select_event_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        layoutManager.setOrientation(layoutManager.HORIZONTAL);
//        selectEventView.setHasFixedSize(true);
//        selectEventView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");

//        selectEventImageAdapter = new SelectEventImageAdapter(getApplicationContext(), imagePath);
//        selectEventView.setAdapter(selectEventImageAdapter);

        /*
        * Viewpager for showing suggested events
        * */
        SelectEventPagerAdapter selectEventPagerAdapter = new SelectEventPagerAdapter(getSupportFragmentManager());
        selectViewPager = (ViewPager) findViewById(R.id.select_event_list);
        selectViewPager.setAdapter(selectEventPagerAdapter);
        selectViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                //position: [-0.75, 1.25]
                float normalizedposition = Math.abs(1 - Math.abs(position - 0.25f));
                page.setAlpha(normalizedposition / 4 + 0.75f);
                page.setScaleX(normalizedposition / 4 + 0.75f);
                page.setScaleY(normalizedposition / 4 + 0.75f);
            }
        });

        refresh();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.select_event_map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    public void refresh() {
        final ProgressDialog dialog = new ProgressDialog(SelectEvent.this);
        dialog.setMessage("로딩 중");
        dialog.show();
        ParseUser user = ParseUser.getCurrentUser();
        List<String> wishlist = user.getList(User.WISHLIST);
        if (wishlist == null) {
            selectViewPager.setAdapter(new SelectEventPagerAdapter(getSupportFragmentManager(), new ArrayList<Event>(), imagePath));
            dialog.dismiss();
        } else {
            ParseQuery<Event> query = Event.getQuery();
            query.whereContainedIn("objectId", wishlist);
            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    if (e == null) {
                        //                    List<Select_Event_Image_item> items = new ArrayList<>();
                        //                    for (Event event : events) {
                        //                        ParseQuery<Stamp> subQuery = Stamp.getQuery();
                        //                        subQuery.whereEqualTo("event", event);
                        //                        try {
                        //                            Stamp stamp = subQuery.getFirst();
                        //                            items.add(new Select_Event_Image_item(event, stamp));
                        //                        } catch (ParseException e1) {
                        //                            e1.printStackTrace();
                        //                        }
                        //                    }
                        selectViewPager.setAdapter(new SelectEventPagerAdapter(getSupportFragmentManager(), events, imagePath));
                        setMarker(events);
                        dialog.dismiss();
                        //                    selectEventImageAdapter.setItems(items);
                        //                    selectEventImageAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectEvent Page", // TODO: Define a title for the content shown.
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SelectEvent Page", // TODO: Define a title for the content shown.
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        float geoPoint[] = new float[2];
        exif.getLatLong(geoPoint);
        String location = "";

        Geocoder gc = new Geocoder(this, Locale.KOREAN);
        try {
            List<Address> addresses = gc.getFromLocation(geoPoint[0], geoPoint[1], 1);
            StringBuffer sb = new StringBuffer();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                sb.append(address.getAddressLine(0).toString());
//                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
//                    sb.append(address.getAddressLine(i)).append("\n");
//                sb.append(address.getCountryName()).append(" ");	// 나라코드
//                sb.append(address.getLocality()).append(" ");		// 시
//                sb.append(address.getSubLocality() + " ");  		// 구
//                sb.append(address.getThoroughfare()).append(" ");	// 동
//                sb.append(address.getFeatureName()).append(" ");	// 번지
                location = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        LatLng loc = new LatLng(geoPoint[0],geoPoint[1]);
        MarkerOptions marker = new MarkerOptions();
        marker.title(location);
        marker.position(loc);

        newStampMarker = mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
    }

    public void setMarker(List<Event> events){
        markers = new ArrayList<>();
        Geocoder gc = new Geocoder(this, Locale.KOREAN);
        for (Event event : events){
            //TODO: set lat long of event
            double latitude = event.getMeanLa();
            double longitude = event.getMeanLo();
            String location = "";
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuffer sb = new StringBuffer();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    sb.append(address.getAddressLine(0).toString());
                    location = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            markers.add(mMap.addMarker((new MarkerOptions()).title(location).position(new LatLng(latitude, longitude)).alpha((float) 0.6)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))));
        }
        markers.add(newStampMarker);

        selectViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Marker m = markers.get(position);
                m.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 14));
                for (int i=0; i<markers.size();i++){
                    if (i == position)
                        m.setAlpha((float)1.0);
                    else
                        markers.get(i).setAlpha((float)0.6);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Marker m = markers.get(0);
        m.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 14));
        for (int i=0; i<markers.size();i++){
            if (i == 0)
                m.setAlpha((float)1.0);
            else
                markers.get(i).setAlpha((float)0.6);
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SelectEventPagerAdapter extends FragmentPagerAdapter {
//        List<Event> events;
//        List<Stamp> stamps;
        private int size;
        public SelectEventPagerAdapter(FragmentManager fm) {
            super(fm);
//            this.events = new ArrayList<>();
//            this.stamps = new ArrayList<>();
            size = 0;
        }
        public SelectEventPagerAdapter(FragmentManager fm, List<Event> events, String imagePath) {
            super(fm);
//            this.events = events;
//            this.stamps = stamps;
            SelectEventFragment.events = events;
            SelectEventFragment.imagePath = imagePath;
            size = events.size() + 1;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == size - 1)
                return SelectEventFragment.newInstance(-1);
            return SelectEventFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
//            return events.size();
            return size;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SelectEventFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static List<Event> events;
        public static String imagePath;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SelectEventFragment newInstance(int sectionNumber) {
            SelectEventFragment fragment = new SelectEventFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SelectEventFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.image_with_title, container, false);
            final int position = getArguments().getInt(ARG_SECTION_NUMBER);
            TextView select_event_title = (TextView) rootView.findViewById(R.id.image_with_title_text);
            ParseImageView select_event_image = (ParseImageView) rootView.findViewById(R.id.image_with_title_view);
            CardView select_event_card = (CardView) rootView.findViewById(R.id.image_with_title_card);
            if(position >= 0) {
                select_event_title.setText(events.get(position).getTitle());
                select_event_image.setParseFile(events.get(position).getThumbnail(1));
                select_event_image.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        //nothing to do
                    }
                });
                select_event_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCreateEventActivity = new Intent(container.getContext(), Create_Event.class);
                        toCreateEventActivity.putExtra("imagePath", imagePath);
                        toCreateEventActivity.putExtra("eventId", events.get(position).getObjectId());
                        toCreateEventActivity.putExtra("eventTitle", events.get(position).getTitle());
                        toCreateEventActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toCreateEventActivity.putExtra("isSelectActivity", "true");
                        container.getContext().startActivity(toCreateEventActivity);
                        getActivity().overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
                    }
                });
            }
            else {
                TextView select_event_add_text = (TextView) rootView.findViewById(R.id.image_with_title_add);
                select_event_add_text.setVisibility(View.VISIBLE);
                select_event_title.setVisibility(View.GONE);
                select_event_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCreateEventActivity = new Intent(container.getContext(), Create_Event.class);
                        toCreateEventActivity.putExtra("imagePath", imagePath);
                        toCreateEventActivity.putExtra("eventId", (String)null);
                        toCreateEventActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toCreateEventActivity.putExtra("isSelectActivity", "true");
                        container.getContext().startActivity(toCreateEventActivity);
                        getActivity().overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
                    }
                });
            }
            return rootView;
        }
    }
}
