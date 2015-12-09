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

public class UserInfoActivity extends AppCompatActivity {

    UserInfoAdapter userInfoAdapter;
    private int friendState;
    public static final int FRIEND_REQUEST = 0;//요청을 받은 거
    public static final int FRIEND_APPLYED = 1;
    public static final int FRIEND_NONE = 2;
    public static final int FRIEND_REJECTED = 3;
    public static final int FRIEND_SEND = 4; //내가 요청보낸 거

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("사용자 정보");

        String userId = getIntent().getStringExtra("user_id");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (friendState == FRIEND_APPLYED){
        } else if (friendState == FRIEND_REQUEST){
            getMenuInflater().inflate(R.menu.user_info_apply_menu, menu);
        } else if (friendState == FRIEND_NONE){
            getMenuInflater().inflate(R.menu.user_info_add_menu, menu);
        } else if (friendState == FRIEND_REJECTED){
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_friend) {
            //TODO: add friend
        }
        else if (id == R.id.action_apply_friend) {
            //TODO: apply friend
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
