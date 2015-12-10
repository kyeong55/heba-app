package com.example.my8;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    UserInfoAdapter userInfoAdapter;
    private int friendState;
    public static final int FRIEND_REQUEST = 0; //요청을 받은 거
    public static final int FRIEND_APPLYED = 1; //이미 친구
    public static final int FRIEND_NONE = 2;    //아무 사이 아님
    public static final int FRIEND_DONE = 3;    //reject했거나 내가 요청을 보냄 (버튼 x)

    String userId;
    Friend friend;
    MenuItem applyButton;
    MenuItem rejectButton;
    MenuItem addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("사용자 정보");

        userId = getIntent().getStringExtra("user_id");
        friendState = getIntent().getIntExtra("already_friend",-1);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float displayWidth = (float)metrics.widthPixels/metrics.xdpi;
        int columnNum = (int) (displayWidth/1.4);
        if (columnNum < 2)
            columnNum = 2;
        else if (columnNum > 5)
            columnNum = 5;

        userInfoAdapter = new UserInfoAdapter(getApplicationContext(), userId, metrics.widthPixels*6/(7*columnNum));

        final GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),columnNum);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = userInfoAdapter.getItemViewType(position);
                if ((viewType == userInfoAdapter.VIEW_TYPE_FOOTER)||(viewType == userInfoAdapter.VIEW_TYPE_HEADER))
                    return layoutManager.getSpanCount();
                else
                    return 1;
            }
        });
        RecyclerView userStampView = (RecyclerView) findViewById(R.id.user_info_recyclerview);
        userStampView.setHasFixedSize(true);
        userStampView.setLayoutManager(layoutManager);
        userStampView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));
        userStampView.setAdapter(userInfoAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.user_info_apply_menu, menu);
        getMenuInflater().inflate(R.menu.user_info_add_menu, menu);

        applyButton = menu.findItem(R.id.action_apply_friend).setVisible(false);
        rejectButton = menu.findItem(R.id.action_reject_friend).setVisible(false);
        addButton = menu.findItem(R.id.action_add_friend).setVisible(false);

        String myUserId = ParseUser.getCurrentUser().getObjectId();

        ParseQuery<Friend> friendToQuery = Friend.getQuery();
        friendToQuery.whereEqualTo(Friend.TO_USER_ID, userId);
        friendToQuery.whereEqualTo(Friend.FROM_USER_ID, myUserId);

        ParseQuery<Friend> friendFromQuery = Friend.getQuery();
        friendFromQuery.whereEqualTo(Friend.FROM_USER_ID, userId);
        friendFromQuery.whereEqualTo(Friend.TO_USER_ID, myUserId);

        List<ParseQuery<Friend>> queries = new ArrayList<>();
        queries.add(friendToQuery);
        queries.add(friendFromQuery);

        ParseQuery<Friend> query = ParseQuery.or(queries);

        query.findInBackground(new FindCallback<Friend>() {
            @Override
            public void done(List<Friend> friends, ParseException e) {
                if (e == null) {
                    if (friends.size() == 0) {
                        //No request
                        addButton.setVisible(true);
                    } else {
                        friend = friends.get(0);

                        switch(friend.getState()) {
                            case Friend.APPROVED:
                                //they are friends
                                break;
                            case Friend.REJECTED:
                                //One of them rejected friend request.
                                break;
                            case Friend.REQUESTED:
                                if (friend.getToUserId() == userId) {
                                    //I requested.
                                } else {
                                    applyButton.setVisible(true);
                                    rejectButton.setVisible(true);
                                }
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_friend) {
            addButton.setVisible(false);
            Friend friend = new Friend(ParseUser.getCurrentUser(), userId);

            ParseACL friendACL = new ParseACL();
            friendACL.setPublicReadAccess(true);
            friendACL.setWriteAccess(userId, true);
            friendACL.setWriteAccess(ParseUser.getCurrentUser().getObjectId(), true);
            friend.setACL(friendACL);

            friend.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "친구 요청 되었습니다", Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) MainActivity.mainActivity;
                        mainActivity.refresh(1);
                    }
                }
            });
        }
        else if (id == R.id.action_apply_friend) {
            applyButton.setVisible(false);
            rejectButton.setVisible(false);
            friend.setState(Friend.APPROVED);
            friend.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "친구 추가 되었습니다", Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) MainActivity.mainActivity;
                        mainActivity.refresh(1);
                    }
                }
            });
        }
        else if (id == R.id.action_reject_friend) {
            applyButton.setVisible(false);
            rejectButton.setVisible(false);
            friend.setState(Friend.REJECTED);
            friend.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "친구 거절 되었습니다", Toast.LENGTH_SHORT).show();
                        MainActivity mainActivity = (MainActivity) MainActivity.mainActivity;
                        mainActivity.refresh(1);
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }

    private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
        LinearLayoutManager llm;

        public ChildAttachListener(LinearLayoutManager llm){
            super();
            this.llm = llm;
        }

        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (userInfoAdapter.getItemCount() - 4 <= llm.findLastVisibleItemPosition()) {
                if(!userInfoAdapter.isAdding() && (!userInfoAdapter.addedAll))
                    userInfoAdapter.add();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    }
}
