package com.example.my8;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Activity mainActivity;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    public static SectionsPagerAdapter mSectionsPagerAdapter;
    final int REQ_CODE_SELECT_STAMP_IMAGE = 100;
    final int REQ_CODE_SELECT_PROFILE_IMAGE = 101;
    final int REQ_CODE_SELECT_COVER_IMAGE = 102;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static NavigationView navigationView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set up the number of holding pages
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    vis();
                else
                    invis();
                if (position == 1) {
                    //refresh(1);
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
                startActivityForResult(intent, REQ_CODE_SELECT_STAMP_IMAGE);
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
    public static void navigationViewRefresh(){
        View header = navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.option_user_name);
        ParseImageView profileImage = (ParseImageView) header.findViewById(R.id.option_profile_image);
        ParseImageView coverImage = (ParseImageView) header.findViewById(R.id.option_cover_image);
        TextView stampCount = (TextView) header.findViewById(R.id.option_stamp_count);
        // TODO: refresh user info (name, profile image, cover, stamp count)
        ParseUser user = ParseUser.getCurrentUser();

        userName.setText(user.getUsername());
        if (user.getList(User.DONELIST) == null) {
            stampCount.setText("0");
        } else {
            stampCount.setText(user.getList(User.DONELIST).size()+"");
        }

        if (user.getBoolean(User.EXIST_PROFILE)) {
            profileImage.setParseFile(user.getParseFile(User.PROFILE));
            profileImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //nothing to do
                }
            });
        }

        if (user.getBoolean(User.EXIST_COVER)) {
            coverImage.setParseFile(user.getParseFile(User.COVER));
            coverImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //nothing to do
                }
            });
        }
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

        if (id == R.id.nav_friends_list) {
            Intent intent = new Intent(this, MyFriendActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
        } else if (id == R.id.nav_profile_image) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_CODE_SELECT_PROFILE_IMAGE);
        } else if (id == R.id.nav_cover_image) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_CODE_SELECT_COVER_IMAGE);
        } else if (id == R.id.nav_passward) {
            // TODO: Edit password
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View dialog_layout = inflater.inflate(R.layout.activity_main_dialog_edit_password,(ViewGroup)findViewById(R.id.dialog_edit_password_root));
            final AlertDialog.Builder editPWDialogBuild = new AlertDialog.Builder(MainActivity.this);
            editPWDialogBuild.setTitle("비밀번호 변경");
            editPWDialogBuild.setView(dialog_layout);
            editPWDialogBuild.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String new_pw = ((TextView) dialog_layout.findViewById(R.id.dialog_new_password)).getText().toString().trim();
                    String new_pw_again = ((TextView) dialog_layout.findViewById(R.id.dialog_new_password_again)).getText().toString().trim();
                    if (new_pw.length() > 0) {
                        if (new_pw.equals(new_pw_again)) {
                            final ProgressDialog editPWProgressDialog = new ProgressDialog(MainActivity.this);
                            editPWProgressDialog.setMessage(getString(R.string.progress_edit_password));
                            editPWProgressDialog.show();
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            currentUser.getCurrentUser().setPassword(new_pw);
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    editPWProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            editPWDialogBuild.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog editPWDialog = editPWDialogBuild.create();
            editPWDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            editPWDialogBuild.create().show();
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
            overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_SELECT_STAMP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String imagePath = getImageNameToUri(data.getData());

                Intent toSelectEventActivity = new Intent(this, SelectEvent.class);
                toSelectEventActivity.putExtra("imagePath", imagePath);
                startActivity(toSelectEventActivity);
                overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);

                /*
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
                */
            }
        }
        else if(requestCode == REQ_CODE_SELECT_PROFILE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: Edit profile image
                String imagePath = getImageNameToUri(data.getData());

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap profile = BitmapFactory.decodeFile(imagePath);
                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

                int rotationAngle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                Bitmap rotatedProfile = Bitmap.createBitmap(profile, 0,
                        0, profile.getWidth(), profile.getHeight(),
                        matrix, true);

                Bitmap scaledRotatedProfile;
                if (rotatedProfile.getWidth() > rotatedProfile.getHeight()) {
                    if (rotatedProfile.getHeight() > 200) {
                        scaledRotatedProfile = Bitmap.createScaledBitmap(rotatedProfile,
                                200 * rotatedProfile.getWidth() / rotatedProfile.getHeight(), 200, false);
                    } else {
                        scaledRotatedProfile = rotatedProfile;
                    }
                } else {
                    if (rotatedProfile.getWidth() > 200) {
                        scaledRotatedProfile = Bitmap.createScaledBitmap(rotatedProfile, 200, 200
                                * rotatedProfile.getHeight() / rotatedProfile.getWidth(), false);
                    } else {
                        scaledRotatedProfile = rotatedProfile;
                    }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                scaledRotatedProfile.compress(Bitmap.CompressFormat.JPEG, 25, bos);
                byte[] Photo = bos.toByteArray();
                ParseFile ProfileFile = new ParseFile("profile.jpg", Photo);
                ProfileFile.saveInBackground();

                ParseUser.getCurrentUser().put(User.EXIST_PROFILE, true);
                ParseUser.getCurrentUser().put(User.PROFILE, ProfileFile);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        refresh(0);
                        refresh(2);
                        refresh(4);
                    }
                });
            }
        }
        else if(requestCode == REQ_CODE_SELECT_COVER_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: Edit cover image
                String imagePath = getImageNameToUri(data.getData());

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap cover = BitmapFactory.decodeFile(imagePath);
                String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

                int rotationAngle = 0;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                Bitmap rotatedCover = Bitmap.createBitmap(cover, 0,
                        0, cover.getWidth(), cover.getHeight(),
                        matrix, true);

                Bitmap scaledRotatedProfile;
                if (rotatedCover.getWidth() > rotatedCover.getHeight()) {
                    if (rotatedCover.getHeight() > 1024) {
                        scaledRotatedProfile = Bitmap.createScaledBitmap(rotatedCover,
                                1024 * rotatedCover.getWidth() / rotatedCover.getHeight(), 1024, false);
                    } else {
                        scaledRotatedProfile = rotatedCover;
                    }
                } else {
                    if (rotatedCover.getWidth() > 1024) {
                        scaledRotatedProfile = Bitmap.createScaledBitmap(rotatedCover, 1024, 1024
                                * rotatedCover.getHeight() / rotatedCover.getWidth(), false);
                    } else {
                        scaledRotatedProfile = rotatedCover;
                    }
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                scaledRotatedProfile.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] Photo = bos.toByteArray();
                ParseFile CoverFile = new ParseFile("cover.jpg", Photo);
                CoverFile.saveInBackground();

                ParseUser.getCurrentUser().put(User.EXIST_COVER, true);
                ParseUser.getCurrentUser().put(User.COVER, CoverFile);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        refresh(2);
                        refresh(4);
                    }
                });
            }
        }
    }

    public String getImageNameToUri(Uri contentUri) {
        verifyPermissions(this);
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("Content URI parsing", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Checks if the app has permissions
     *
     * If the app does not has permissions then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission
        String[] permissions = new String[]{
                Manifest.permission_group.LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        boolean haveAllPermissions = true;
        for (String permission : permissions) {
            boolean havePermission = ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            haveAllPermissions = haveAllPermissions && havePermission;
        }
        if (!haveAllPermissions) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission_group.LOCATION
                    }, 1);
        }
    }

    public static void refresh(int position) {
        switch (position) {
            case 0: //playground
                ((PlaygroundFragment) mSectionsPagerAdapter.getItem(position)).refresh();
                break;
            case 1: //friend
                ((FriendsFragment) mSectionsPagerAdapter.getItem(position)).refresh();
                break;
            case 2: //my stamp
                ((MyStampFragment) mSectionsPagerAdapter.getItem(position)).refresh();
                break;
            case 3: //wishlist
                ((WishlistFragment) mSectionsPagerAdapter.getItem(position)).refresh();
                break;
            case 4: //
                navigationViewRefresh();
        }
    }
    public static void refreshAll(){
        ((PlaygroundFragment) mSectionsPagerAdapter.getItem(0)).refresh();
        ((FriendsFragment) mSectionsPagerAdapter.getItem(1)).refresh();
        ((MyStampFragment) mSectionsPagerAdapter.getItem(2)).refresh();
        ((WishlistFragment) mSectionsPagerAdapter.getItem(3)).refresh();
        navigationViewRefresh();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        PlaygroundFragment pgFragment;
        FriendsFragment fFragment;
        MyStampFragment msFragment;
        WishlistFragment wlFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pgFragment = new PlaygroundFragment();
            fFragment = new FriendsFragment();
            msFragment = new MyStampFragment();
            wlFragment = new WishlistFragment();
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
                case 3:
                    return wlFragment;
            }
            return null;
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
        private RecyclerView pgView;
        private PgAdapter pgadapter;
        private SwipeRefreshLayout mySwipeRefreshLayout;
        private ViewGroup container;

        public PlaygroundFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            this.container = container;
            LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

            View rootView = inflater.inflate(R.layout.playground, container, false);
            pgView = (RecyclerView) rootView.findViewById(R.id.pg_view);
            pgView.setHasFixedSize(true);
            pgView.setLayoutManager(layoutManager);
            pgView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));

            pgadapter = new PgAdapter(container);
            pgView.setAdapter(pgadapter);

            mySwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.pg_swipe_layout);
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // This method performs the actual data-refresh operation.
                            refresh();
                        }
                    }
            );
            mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
            refresh();
            return rootView;
        }

        public void refresh() {
            pgadapter.setIsAdding(true);
            String userId = ParseUser.getCurrentUser().getObjectId();

            ParseQuery<Friend> friendToQuery = Friend.getQuery();
            friendToQuery.whereEqualTo(Friend.STATE, Friend.APPROVED);
            friendToQuery.whereEqualTo(Friend.TO_USER_ID, userId);

            ParseQuery<ParseUser> userToQuery = ParseUser.getQuery();
            userToQuery.whereMatchesKeyInQuery(User.ID, Friend.FROM_USER_ID, friendToQuery);

            ParseQuery<Friend> friendFromQuery = Friend.getQuery();
            friendFromQuery.whereEqualTo(Friend.STATE, Friend.APPROVED);
            friendFromQuery.whereEqualTo(Friend.FROM_USER_ID, userId);

            ParseQuery<ParseUser> userFromQuery = ParseUser.getQuery();
            userFromQuery.whereMatchesKeyInQuery(User.ID, Friend.TO_USER_ID, friendFromQuery);

            List<ParseQuery<ParseUser>> userQueries = new ArrayList<ParseQuery<ParseUser>>();
            userQueries.add(userToQuery);
            userQueries.add(userFromQuery);

            ParseQuery<ParseUser> userQuery = ParseQuery.or(userQueries);

            ParseQuery<ActionContract> friendActionQuery = ActionContract.getQuery();
            friendActionQuery.whereMatchesQuery(ActionContract.USER, userQuery);

            ParseQuery<ActionContract> myActionQuery = ActionContract.getQuery();
            myActionQuery.whereEqualTo(ActionContract.USER, ParseUser.getCurrentUser());

            List<ParseQuery<ActionContract>> queries = new ArrayList<ParseQuery<ActionContract>>();
            queries.add(friendActionQuery);
            queries.add(myActionQuery);

            ParseQuery<ActionContract> query = ParseQuery.or(queries);
            query.orderByDescending("updatedAt");
            query.setLimit(5);
            query.include(ActionContract.USER);
            query.include(ActionContract.EVENT);
            query.include(ActionContract.EVENT + "." + "stamp0");
            query.include(ActionContract.EVENT + "." + "stamp1");
            query.include(ActionContract.EVENT + "." + "stamp2");
            query.include(ActionContract.EVENT + "." + "stamp3");
            query.include(ActionContract.EVENT + "." + "stamp4");
            query.include(ActionContract.EVENT + "." + "stamp5");
            query.findInBackground(new FindCallback<ActionContract>() {
                @Override
                public void done(List<ActionContract> actions, ParseException e) {
                    if (e == null) {
                        List<Playground_item> items = new ArrayList<>();
                        List<Event> checkList = new ArrayList<>();
                        int idx = 1;
                        for (ActionContract action : actions) {
                            if (idx == 1)
                                pgadapter.setLastUpdate(action.getUpdatedAt());
                            if (!checkList.contains(action.getEvent())) {
                                checkList.add(action.getEvent());
                                items.add(new Playground_item(container, action, container.getContext()));
                            }
                            if (idx == actions.size())
                                pgadapter.setLastUpdate(action.getUpdatedAt());
                            idx = idx + 1;
                        }
                        pgadapter.setCheckList(checkList);
                        pgadapter.setItems(items);
                        pgadapter.notifyDataSetChanged();
                        mySwipeRefreshLayout.setRefreshing(false);

                        pgadapter.addedAll = false;
                        pgadapter.setIsAdding(false);
                    }
                }
            });
        }

        private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
            LinearLayoutManager llm;

            public ChildAttachListener(LinearLayoutManager llm){
                super();
                this.llm = llm;
            }

            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (pgadapter.getItemCount() - 3 <= llm.findLastVisibleItemPosition()) {
                    if(!pgadapter.isAdding() && (!pgadapter.addedAll))
                        pgadapter.add();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        }
    }

    /**
     * My Stamp fragment
     */
    public static class MyStampFragment extends Fragment {
        private View header;
        private MyStampAdapter msAdapter;

        private List<MyStamp_item> items = new ArrayList<>();

        public MyStampFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.my_stamp, container, false);
            header = inflater.inflate(R.layout.my_stamp_header, container, false);

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            float displayWidth = (float)metrics.widthPixels/metrics.xdpi;
            int columnNum = (int) (displayWidth/1.4);
            if (columnNum < 2)
                columnNum = 2;
            else if (columnNum > 5)
                columnNum = 5;

            msAdapter = new MyStampAdapter(container.getContext(),header, metrics.widthPixels*6/(7*columnNum));

            final GridLayoutManager layoutManager = new GridLayoutManager(container.getContext(),columnNum);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = msAdapter.getItemViewType(position);
                    if ((viewType == msAdapter.VIEW_TYPE_HEADER)||(viewType == msAdapter.VIEW_TYPE_FOOTER))
                        return layoutManager.getSpanCount();
                    else
                        return 1;
                }
            });
            RecyclerView events = (RecyclerView) rootView.findViewById(R.id.ms_view);
            events.setHasFixedSize(true);
            events.setLayoutManager(layoutManager);
            events.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));
            events.setAdapter(msAdapter);

            refresh();

            return rootView;
        }

        public void refresh() {
            if (header == null) return;
            msAdapter.setisAdding(true);
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
            ParseQuery<Stamp> query = Stamp.getQuery();
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
            query.setLimit(8);
            query.findInBackground(new FindCallback<Stamp>() {
                @Override
                public void done(List<Stamp> stamps, ParseException e) {
                    if (e == null) {
                        List<MyStamp_item> newItems = new ArrayList<>();
                        for (Stamp stamp : stamps)
                            newItems.add(new MyStamp_item(stamp));
                        items = newItems;
                        msAdapter.setItems(items);
                        msAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(),
                                "Error on retrieving MyStamp: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    msAdapter.addedAll = false;
                    msAdapter.setisAdding(false);
                }
            });
        }

        private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
            LinearLayoutManager llm;

            public ChildAttachListener(LinearLayoutManager llm){
                super();
                this.llm = llm;
            }

            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (msAdapter.getItemCount() - 4 <= llm.findLastVisibleItemPosition()) {
                    if(!msAdapter.isAdding() && (!msAdapter.addedAll))
                        msAdapter.add();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        }
    }

    /**
     * Friends Fragment
     */
    public static class FriendsFragment extends Fragment {
        private RecyclerView friendsView;
        private FriendsAdapter friendsAdapter;
        private SwipeRefreshLayout mySwipeRefreshLayout;

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
            friendsView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));

            friendsAdapter = new FriendsAdapter(container.getContext());
            friendsView.setAdapter(friendsAdapter);

            mySwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.friends_swipe_layout);
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // This method performs the actual data-refresh operation.
                            refresh();
                        }
                    }
            );
            mySwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
            refresh();
            return rootView;
        }

        public void refresh() {
            friendsAdapter.setIsAdding(true);
            ParseQuery<Friend> query = Friend.getQuery();
            query.whereEqualTo(Friend.TO_USER_ID, ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo(Friend.STATE, Friend.REQUESTED);
            query.include(Friend.FROM_USER);
            query.findInBackground(new FindCallback<Friend>() {
                @Override
                public void done(List<Friend> tuples, ParseException e) {
                    if (e == null) {
                        List<Friends_item> items = new ArrayList<>();
                        for (Friend tuple : tuples) {
                            items.add(new Friends_item(tuple, tuple.getFromUser()));
                        }
                        friendsAdapter.setItems(items);
                        friendsAdapter.setNRequest(items.size());
                        friendsAdapter.notifyDataSetChanged();

                        friendsAdapter.addedAll = false;
                        friendsAdapter.setIsAdding(false);
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }

        private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
            LinearLayoutManager llm;

            public ChildAttachListener(LinearLayoutManager llm){
                super();
                this.llm = llm;
            }

            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (friendsAdapter.getItemCount() - 3 <= llm.findLastVisibleItemPosition()) {
                    if(!friendsAdapter.isAdding() &&(!friendsAdapter.addedAll)) {
                        friendsAdapter.add();
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        }
    }

    /**
     * Wishlist fragment
     */
    public static class WishlistFragment extends Fragment {
        private RecyclerView wlView;
        private WishlistAdapter wlAdapter;

        public WishlistFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.wishlist, container, false);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());

            wlView = (RecyclerView) rootView.findViewById(R.id.wishlist_view);
            wlView.setHasFixedSize(true);
            wlView.setLayoutManager(layoutManager);
            wlView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));

            wlAdapter = new WishlistAdapter(container.getContext());
            wlView.setAdapter(wlAdapter);
            refresh();
            return rootView;
        }

        public void refresh() {
            wlAdapter.setIsAdding(true);
            ParseUser user = ParseUser.getCurrentUser();
            List<String> wishlist = user.getList(User.WISHLIST);
            if (wishlist != null) {
                ParseQuery<Event> query = Event.getQuery();
                query.whereContainedIn("objectId", wishlist);
                query.orderByDescending("title");
                query.setLimit(5);
                query.findInBackground(new FindCallback<Event>() {
                    @Override
                    public void done(List<Event> events, ParseException e) {
                        if (e == null) {
                            List<Wishlist_item> items = new ArrayList<>();
                            for (Event event : events) {
                                items.add(new Wishlist_item(event));
                            }
                            wlAdapter.setItems(items);
                            wlAdapter.notifyDataSetChanged();

                            wlAdapter.addedAll = false;
                            wlAdapter.setIsAdding(false);
                        }
                    }
                });
            }
        }

        private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
            LinearLayoutManager llm;

            public ChildAttachListener(LinearLayoutManager llm){
                super();
                this.llm = llm;
            }

            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (wlAdapter.getItemCount() - 3 <= llm.findLastVisibleItemPosition()) {
                    if(!wlAdapter.isAdding() && (!wlAdapter.addedAll))
                        wlAdapter.add();
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        }
//        private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
//            LinearLayoutManager llm;
//
//            public ChildAttachListener(LinearLayoutManager llm){
//                super();
//                this.llm = llm;
//            }
//
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//                if (items.size() - 2 <= llm.findLastVisibleItemPosition()) {
//                    if(!wlAdapter.isAdding() && (items.size()>=5)&&(!wlAdapter.addedAll))
//                        wlAdapter.add();
//                }
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//
//            }
//        }
    }

}
