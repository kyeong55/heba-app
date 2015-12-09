package com.example.my8;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MyFriendActivity extends AppCompatActivity {
    private MyFriendAdapter myFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("내 친구");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView myFriendView = (RecyclerView) findViewById(R.id.my_friend_list);
        myFriendView.setHasFixedSize(true);
        myFriendView.setLayoutManager(layoutManager);
        myFriendView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));

        myFriendAdapter = new MyFriendAdapter(getApplicationContext());
        myFriendView.setAdapter(myFriendAdapter);
        myFriendAdapter.add();
    }

    private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
        LinearLayoutManager llm;

        public ChildAttachListener(LinearLayoutManager llm){
            super();
            this.llm = llm;
        }

        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (myFriendAdapter.getItemCount() - 3 <= llm.findLastVisibleItemPosition()) {
                if(!myFriendAdapter.isAdding() && (!myFriendAdapter.addedAll)) {
                    myFriendAdapter.add();
                }
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
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
}
