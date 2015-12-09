package com.example.my8;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.parse.ParseUser;

public class UserInfoActivity extends AppCompatActivity {

    UserInfoAdapter userInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("친구 정보");

        String userId = getIntent().getStringExtra("user_id");

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
