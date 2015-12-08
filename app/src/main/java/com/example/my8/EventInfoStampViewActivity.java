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
import android.view.View;
import android.view.WindowManager;

public class EventInfoStampViewActivity extends AppCompatActivity {

    EventInfoStampAdapter eventInfoStampAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info_stamp_view);

        String eventId = getIntent().getStringExtra("eventId");
        String eventTitle = getIntent().getStringExtra("eventTitle");

        Toolbar toolbar = (Toolbar) findViewById(R.id.event_info_stamp_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(eventTitle);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.event_info_stamp_view_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float displayWidth = (float)metrics.widthPixels/metrics.xdpi;
        int columnNum = (int) (displayWidth/1.4);
        if (columnNum < 2)
            columnNum = 2;
        else if (columnNum > 5)
            columnNum = 5;

        eventInfoStampAdapter = new EventInfoStampAdapter(getApplicationContext(),eventId,metrics.widthPixels*6/(7*columnNum));

        final GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),columnNum);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = eventInfoStampAdapter.getItemViewType(position);
                if (viewType == eventInfoStampAdapter.VIEW_TYPE_FOOTER)
                    return layoutManager.getSpanCount();
                else
                    return 1;
            }
        });
        RecyclerView stampView = (RecyclerView) findViewById(R.id.event_info_stamp_view_grid);
        stampView.setHasFixedSize(true);
        stampView.setLayoutManager(layoutManager);
        stampView.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager));
        stampView.setAdapter(eventInfoStampAdapter);
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
            if (eventInfoStampAdapter.getItemCount() - 4 <= llm.findLastVisibleItemPosition()) {
                if(!eventInfoStampAdapter.isAdding() && (!eventInfoStampAdapter.addedAll))
                    eventInfoStampAdapter.add();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    }
}
