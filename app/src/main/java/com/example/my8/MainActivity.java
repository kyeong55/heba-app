package com.example.my8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseUser.logOut;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    final int REQ_CODE_SELECT_IMAGE = 100;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    NavigationView navigationView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set up the number of holding pages
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    vis();
                else
                    invis();
                if (position == 2) {
                    // TODO only refresh when new stamp added or profile updated
                    ((MyStampFragment)mSectionsPagerAdapter.getItem(position)).refresh();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewRefresh();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // "+" button appears
    public void vis() {
        View myView = findViewById(R.id.fab);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.button_visible);
        if (myView.getVisibility() == View.INVISIBLE) {
            myView.setVisibility(View.VISIBLE);
            myView.startAnimation(ani);
        }
    }

    // "+" button disappears
    public void invis() {
        View myView = findViewById(R.id.fab);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.button_invisible);
        if (myView.getVisibility() == View.VISIBLE) {
            myView.setVisibility(View.INVISIBLE);
            myView.startAnimation(ani);
        }
    }

    // Refresh user info in option menu
    public void navigationViewRefresh(){
        View header = navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.option_user_name);
        ImageView profileImage = (ImageView) header.findViewById(R.id.option_profile_image);
        ImageView coverImage = (ImageView) header.findViewById(R.id.option_cover_image);
        TextView stampCount = (TextView) header.findViewById(R.id.option_stamp_count);
        // TODO: refresh user info (name, profile image, cover, stamp count)
        ParseUser user = ParseUser.getCurrentUser();
        userName.setText(user.getUsername());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(mViewPager.getCurrentItem()>0) {
            mViewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile_image) {
            // TODO: Edit profile image
            navigationViewRefresh();
        } else if (id == R.id.nav_cover_image) {
            // TODO: Edit cover image
            navigationViewRefresh();
        } else if (id == R.id.nav_passward) {
            // TODO: Edit passward
        } else if (id == R.id.nav_logout) {
            // Call the Parse log out method
            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage(getString(R.string.progress_logout));
            dialog.show();
            ParseUser.logOutInBackground( new LogOutCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            // Start and intent for the dispatch activity
//            Intent intent = new Intent(this, DispatchActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_search) {
            //TODO: search
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    // Get image path from uri
                    String img_path = getImageNameToUri(data.getData());
                    // Extract geopoint of the image
                    ExifInterface exif = new ExifInterface(img_path);
                    float geopoint[] = new float[2];
                    exif.getLatLong(geopoint);
                    // Build up a intent & start new activity
                    Intent toSelectEventActivity = new Intent(this, SelectEvent.class);
                    toSelectEventActivity.putExtra("latitude", geopoint[0] + "");
                    toSelectEventActivity.putExtra("longitude", geopoint[1] + "");
                    toSelectEventActivity.putExtra("datetime", exif.getAttribute(ExifInterface.TAG_DATETIME));
                    toSelectEventActivity.putExtra("image_path", img_path);
                    startActivity(toSelectEventActivity);
                } catch (FileNotFoundException e) {
                    // auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        PlaygroundFragment pgFragment;
        FriendsFragment fFragment;
        MyStampFragment msFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pgFragment = new PlaygroundFragment();
            fFragment = new FriendsFragment();
            msFragment = new MyStampFragment();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return pgFragment;
                case 1:
                    return fFragment;
                case 2:
                    return msFragment;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "놀이터";
                case 1:
                    return "친구";
                case 2:
                    return "스탬프";
                case 3:
                    return "위시리스트";
            }
            return null;
        }
    }

    /**
     * Playground fragment
     */
    public static class PlaygroundFragment extends Fragment {
        RecyclerView pgView;
        PgAdapter pgadapter;
        SwipeRefreshLayout mySwipeRefreshLayout;

        public PlaygroundFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

            View rootView = inflater.inflate(R.layout.playground, container, false);
            pgView = (RecyclerView) rootView.findViewById(R.id.pg_view);
            pgView.setHasFixedSize(true);
            pgView.setLayoutManager(layoutManager);

            pgadapter = new PgAdapter(container);
            pgView.setAdapter(pgadapter);



            mySwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.pg_swipe_layout);
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // This method performs the actual data-refresh operation.
                            refresh(container, true);
                        }
                    }
            );
            mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark);
            refresh(container, false);
            return rootView;
        }

        public void refresh(final ViewGroup container, final boolean onRefresh) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
            query.orderByDescending("updatedAt");
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        List<Playground_item> items = new ArrayList<>();
                        for (ParseObject event : objects) {
                            Event test = (Event) event;
                            String test_msg = test.getTitle();
                            Log.w("debugging", test_msg);
                            items.add(new Playground_item(container, (Event) event));
                        }
                        Log.w("debugging", "done");
                        pgadapter.setItems(items);
                        pgadapter.notifyDataSetChanged();
                        if (onRefresh) {
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
            });
        }

        public void addEventToPG(ViewGroup container) {
            Playground_item newItem = new Playground_item(88, "새로 추가된거", "이태경", 2015, container.getContext());
            pgadapter.add(newItem);
            pgadapter.notifyDataSetChanged();
        }

    }

    /**
     * My Stamp fragment
     */
    public static class MyStampFragment extends Fragment {
        public View header;
        public MyStampAdapter msAdapter;

        public MyStampFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.my_stamp, container, false);
            header = inflater.inflate(R.layout.my_stamp_header, container, false);

            final GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),2);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? layoutManager.getSpanCount() : 1;
                }
            });
            RecyclerView events = (RecyclerView) rootView.findViewById(R.id.ms_view);
            events.setHasFixedSize(true);
            events.setLayoutManager(layoutManager);

            msAdapter = new MyStampAdapter(container.getContext(),header);
            events.setAdapter(msAdapter);

            refresh();

            return rootView;
        }

        public void refresh() {
            if (header == null) return;
            ParseImageView profile = (ParseImageView) header.findViewById(R.id.ms_profile_image); // profile 사진
            ParseImageView cover = (ParseImageView) header.findViewById(R.id.ms_cover_image); // cover 사진
            TextView user_name = (TextView) header.findViewById(R.id.ms_user_name); // user 이름
            final TextView stamp_count = (TextView) header.findViewById(R.id.ms_stamp_count); // stamp 개수

            ParseUser user = ParseUser.getCurrentUser();
            user_name.setText(user.getUsername());
            // TODO update cover and profile photo
            // profile.setParseFile(user.getProfile());
            // profile.loadInBackground();
            // cover.setParseFile(user.getCover());
            // cover.loadInBackground();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Stamp");
            query.whereEqualTo(Stamp.USER, user);
            query.orderByDescending("updatedAt");
//            query.countInBackground(new CountCallback() {
//                @Override
//                public void done(int count, ParseException e) {
//                    if (e == null) {
//                        stamp_count.setText(count);
//                    }
//                }
//            });
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        List<MyStamp_item> items = new ArrayList<>();
                        for (ParseObject stampObj : objects) {
                            Stamp stamp = (Stamp) stampObj;
                            items.add(new MyStamp_item(stamp));
                        }
                        msAdapter.setItems(items);
                        msAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(),
                                "Error on retrieving MyStamp: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Friends Fragment
     */
    public static class FriendsFragment extends Fragment {
        RecyclerView friendsView;
        FriendsAdapter friendsAdapter;
        SwipeRefreshLayout mySwipeRefreshLayout;

        public FriendsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

            View rootView = inflater.inflate(R.layout.friends, container, false);
            friendsView = (RecyclerView) rootView.findViewById(R.id.friends_view);
            friendsView.setHasFixedSize(true);
            friendsView.setLayoutManager(layoutManager);

            friendsAdapter = new FriendsAdapter(container.getContext());
            friendsView.setAdapter(friendsAdapter);

            mySwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.friends_swipe_layout);
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // This method performs the actual data-refresh operation.
//                            refresh(container, true);
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
            );
            mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
//            refresh(container, false);
            friendsAdapter.add();
            friendsAdapter.add();
            return rootView;
        }

//        public void refresh(final ViewGroup container, final boolean onRefresh) {
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
//            query.orderByDescending("updatedAt");
//            query.setLimit(10);
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//                    if (e == null) {
//                        List<Playground_item> items = new ArrayList<>();
//                        for (ParseObject event : objects) {
//                            Event test = (Event) event;
//                            String test_msg = test.getTitle();
//                            Log.w("debugging", test_msg);
//                            items.add(new Playground_item(container.getContext(), (Event) event));
//                        }
//                        Log.w("debugging", "done");
//                        pgadapter.setItems(items);
//                        pgadapter.notifyDataSetChanged();
//                        if (onRefresh) {
//                            mySwipeRefreshLayout.setRefreshing(false);
//                        }
//                    }
//                }
//            });
//        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
