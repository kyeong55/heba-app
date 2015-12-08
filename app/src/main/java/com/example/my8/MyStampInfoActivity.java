package com.example.my8;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class MyStampInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_stamp_info);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("clicked_stamp_pos", 0);
        ArrayList<String> stampIdList = intent.getStringArrayListExtra("stamp_id_list");

        // Create the adapter that will return a fragment for each activity.
        StampInfoPagerAdapter mStampInfoPagerAdapter = new StampInfoPagerAdapter(getSupportFragmentManager(), stampIdList);

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
    public class StampInfoPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> stampIdList;
        public StampInfoPagerAdapter(FragmentManager fm, ArrayList<String> stampIdList) {
            super(fm);
            this.stampIdList = stampIdList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StampInfoFragment.newInstance(stampIdList.get(position));
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
    public static class StampInfoFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private boolean is_text_visible = true;
        private static final String ARG_STAMP_ID = "stamp_ID";
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StampInfoFragment newInstance(String stampID) {
            StampInfoFragment fragment = new StampInfoFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STAMP_ID, stampID);
            fragment.setArguments(args);
            return fragment;
        }
        public StampInfoFragment () {}

        @SuppressWarnings("unchecked")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.my_stamp_info_component, container, false);
            final ParseImageView imageView = (ParseImageView) rootView.findViewById(R.id.stamp_info_image);
            final TextView textView = (TextView) rootView.findViewById(R.id.stamp_info_text);
            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.stamp_info_progressbar);
            final LinearLayout llayout = (LinearLayout) rootView.findViewById(R.id.stamp_info_linear_layout);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (is_text_visible) {
                        Animation alphaAni = AnimationUtils.loadAnimation(getContext(), R.anim.text_invisible);
                        llayout.startAnimation(alphaAni);
                        llayout.setVisibility(View.INVISIBLE);
                    } else {
                        Animation alphaAni = AnimationUtils.loadAnimation(getContext(), R.anim.text_visible);
                        llayout.startAnimation(alphaAni);
                        llayout.setVisibility(View.VISIBLE);
                    }
                    is_text_visible = !is_text_visible;
                }
            });
            // recall stamp from the server
            ParseQuery<Stamp> query = Stamp.getQuery();
            query.getInBackground(getArguments().getString(ARG_STAMP_ID), new GetCallback<Stamp>() {
                @Override
                public void done(Stamp stamp, ParseException e) {
                    imageView.setParseFile(stamp.getThumbnail());
                    imageView.loadInBackground();
                    textView.setText(stamp.getComment());
                    progressBar.setVisibility(View.GONE);
                }
            });

            return rootView;
        }
    }
}
