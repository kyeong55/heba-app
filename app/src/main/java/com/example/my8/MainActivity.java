package com.example.my8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    final int REQ_CODE_SELECT_IMAGE = 100;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    vis();
                else
                    invis();
            }
            @Override public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state) {}
        });
    }
    // "+" button appears
    public void vis(){
        View myView = findViewById(R.id.fab);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.button_visible);
        if(myView.getVisibility()==View.INVISIBLE) {
            myView.setVisibility(View.VISIBLE);
            myView.startAnimation(ani);
        }
    }
    // "+" button disappears
    public void invis(){
        View myView = findViewById(R.id.fab);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.button_invisible);
        if(myView.getVisibility()==View.VISIBLE) {
            myView.setVisibility(View.INVISIBLE);
            myView.startAnimation(ani);
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                try {
                    // Get image path from uri
                    String img_path = getImageNameToUri(data.getData());
                    // Extract geopoint of the image
                    ExifInterface exif = new ExifInterface(img_path);
                    float geopoint[] = new float[2];
                    exif.getLatLong(geopoint);
                    // Build up a intent & start new activity
                    Intent intent2 = new Intent(this, SelectEvent.class);
                    intent2.putExtra("latitude", geopoint[0] + "");
                    intent2.putExtra("longitude", geopoint[1] + "");
                    intent2.putExtra("datetime",  getTagString(ExifInterface.TAG_DATETIME, exif));
                    intent2.putExtra("image_path", img_path);
                    startActivity(intent2);
                } catch (FileNotFoundException e) {
                    // auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);

        return imgPath;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {super(fm);}
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return new PlaygroundFragment();
                case 2:
                    return new MyStampFragment();
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
                case 0: return "놀이터";
                case 1: return "친구";
                case 2: return "스탬프";
                case 3: return "위시리스트";
            }
            return null;
        }
    }

    /**
     * Playground fragment
     */
    public static class PlaygroundFragment extends Fragment{
        public PlaygroundFragment(){}
        PgAdapter pgadapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.playground, container, false);
            final ViewGroup c = container;
            LinearLayoutManager layoutManager=new LinearLayoutManager(container.getContext());
            RecyclerView events = (RecyclerView) rootView.findViewById(R.id.pg_view);
            events.setHasFixedSize(true);
            events.setLayoutManager(layoutManager);
            List<Playground_item> items = new ArrayList<>();
            // TODO Fetch event list from the server
            items.add(new Playground_item(container.getContext()));
            items.add(new Playground_item(container.getContext()));
            pgadapter = new PgAdapter(container.getContext(),items,R.layout.playground);
            events.setAdapter(pgadapter);
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.refresh);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEventToPG(c);
                }
            });
            return rootView;
        }
        public void addEventToPG(ViewGroup container){
            Playground_item newItem = new Playground_item(88,"새로 추가된거","이태경",2015,container.getContext());
            pgadapter.add(newItem);
        }
    }

    /**
     * My Stamp fragment
     */
    public static class MyStampFragment extends Fragment{
        public MyStampFragment(){}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.my_stamp, container, false);
            ImageView profile = (ImageView) rootView.findViewById(R.id.ms_profile_image);//profile 사진
            ImageView cover = (ImageView) rootView.findViewById(R.id.ms_cover_image);//cover 사진
            TextView user_name = (TextView) rootView.findViewById(R.id.ms_user_name);//user 이름
            TextView stamp_count = (TextView) rootView.findViewById(R.id.ms_stamp_count);//stamp 개수

//            GridLayoutManager layoutManager = new Grid
//            LinearLayoutManager layoutManager=new LinearLayoutManager(container.getContext());
//            RecyclerView events = (RecyclerView) rootView.findViewById(R.id.pg_view);
//            events.setHasFixedSize(true);
//            events.setLayoutManager(layoutManager);
            return rootView;
        }
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
