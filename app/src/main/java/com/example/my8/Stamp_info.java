package com.example.my8;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Stamp_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_info);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("currentstamppos", 0);
        int[] stampIdList = intent.getIntArrayExtra("stamplistid");

        // Create the adapter that will return a fragment for each activity.
        StampInfoPagerAdapter mStampInfoPagerAdapter = new StampInfoPagerAdapter(getSupportFragmentManager(),stampIdList);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mStampInfoPagerAdapter);
        // Set up the number of holding pages
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(pos);

        //todo get image_id  from main activity
        //Intent intent = getIntent();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class StampInfoPagerAdapter extends FragmentPagerAdapter {

        int [] idList;
        public StampInfoPagerAdapter(FragmentManager fm,int[] idList) {
            super(fm);
            this.idList = idList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StampInfoFragment.newInstance(idList[position]);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return idList.length;
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StampInfoFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_STAMP_ID = "stamp_ID";
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StampInfoFragment newInstance(int stampID) {
            StampInfoFragment fragment = new StampInfoFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_STAMP_ID, stampID);
            fragment.setArguments(args);
            return fragment;
        }
        public StampInfoFragment () {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_stamp_info_component, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.stamp_info_text);
            if(getArguments().getInt(ARG_STAMP_ID)!=3)
                textView.setText("Stamp ID: "+getArguments().getInt(ARG_STAMP_ID));
            return rootView;
        }
    }
}
