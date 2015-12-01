package com.example.my8;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class PgStampInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg_stamp_info);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("clicked_stamp_pos", 0);
        ArrayList<String> stampIdList = intent.getStringArrayListExtra("stamp_id_list");

        // Create the adapter that will return a fragment for each activity.
        pgStampInfoPagerAdapter mStampInfoPagerAdapter = new pgStampInfoPagerAdapter(getSupportFragmentManager(), stampIdList);

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mStampInfoPagerAdapter);
        // Set up the number of holding pages
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(pos);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class pgStampInfoPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> stampIdList;
        public pgStampInfoPagerAdapter(FragmentManager fm, ArrayList<String> stampIdList) {
            super(fm);
            this.stampIdList = stampIdList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return pgStampInfoFragment.newInstance(stampIdList.get(position));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return stampIdList.size();
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class pgStampInfoFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_STAMP_ID = "stamp_ID";
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static pgStampInfoFragment newInstance(String stampID) {
            pgStampInfoFragment fragment = new pgStampInfoFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STAMP_ID, stampID);
            fragment.setArguments(args);
            return fragment;
        }
        public pgStampInfoFragment () {}

        @SuppressWarnings("unchecked")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.my_stamp_info_component, container, false);
            final ParseImageView imageView = (ParseImageView) rootView.findViewById(R.id.stamp_info_image);
            final TextView textView = (TextView) rootView.findViewById(R.id.stamp_info_text);
            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.stamp_info_progressbar);

            // recall stamp from the server
            ParseQuery query = new ParseQuery(Stamp.CLASSNAME);
            query.getInBackground(getArguments().getString(ARG_STAMP_ID), new GetCallback<Stamp>() {
                @Override
                public void done(Stamp stamp, ParseException e) {
                    imageView.setParseFile(stamp.getPhotoFile());
                    imageView.loadInBackground();
                    textView.setText(stamp.getComment());
                    progressBar.setVisibility(View.GONE);
                }
            });

            return rootView;
        }
    }
}
