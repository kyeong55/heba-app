package com.example.my8;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
implements SearchView.OnQueryTextListener{

    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SearchView searchView = (SearchView)findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float displayWidth = (float)metrics.widthPixels/metrics.xdpi;
        int columnNum = (int) (displayWidth/1.4);
        if (columnNum < 2)
            columnNum = 2;
        else if (columnNum > 4)
            columnNum = 4;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView searchResult = (RecyclerView) findViewById(R.id.search_recycler_view);
        searchResult.setHasFixedSize(true);
        searchResult.setLayoutManager(layoutManager);

        searchAdapter = new SearchAdapter(getApplicationContext(),metrics.widthPixels*6/(7*columnNum), columnNum);

        searchResult.setAdapter(searchAdapter);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.orderByDescending(User.DONE);
        userQuery.setLimit(3);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                ParseQuery<Event> eventQuery = Event.getQuery();
                eventQuery.orderByDescending(Event.PARTICIPANT);
                eventQuery.setLimit(3);
                List<Event> events = new ArrayList<>();
                try {
                    events = eventQuery.find();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                List<Search_item> items = new ArrayList<>();
                items.add(new Search_item(getString(R.string.search_subtitle_friend)));
                for (ParseUser user : users) {
                    items.add(new Search_item(user));
                }
                items.add(new Search_item(getString(R.string.search_subtitle_event)));
                for (Event event : events) {
                    items.add(new Search_item(event));
                }
                searchAdapter.setItems(items);
                searchAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(final String query) {
//        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
//        searchView.setQuery("", false);
//        searchView.setIconified(true);
        // TODO: search after submit
        if (query != "") {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereStartsWith(User.NAME, query);
            userQuery.orderByAscending(User.DONE);
            userQuery.setLimit(12);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    ParseQuery<Event> eventQuery = Event.getQuery();
                    eventQuery.whereStartsWith(Event.TITLE, query);
                    eventQuery.orderByDescending(Event.PARTICIPANT);
                    List<Event> events = new ArrayList<>();
                    try {
                        events = eventQuery.find();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    List<Search_item> items = new ArrayList<>();
                    items.add(new Search_item(getString(R.string.search_subtitle_friend)));
                    for (ParseUser user : users) {
                        items.add(new Search_item(user));
                    }
                    items.add(new Search_item(getString(R.string.search_subtitle_event)));
                    for (Event event : events) {
                        items.add(new Search_item(event));
                    }
                    searchAdapter.setItems(items);
                    searchAdapter.notifyDataSetChanged();
                }
            });
        }

        //Toast.makeText(this, "Search: "+query, Toast.LENGTH_LONG).show();
        return false;
    }
    @Override
    public boolean onQueryTextChange(final String newText) {
        // TODO: auto search
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereStartsWith(User.NAME, newText);
        userQuery.orderByAscending(User.DONE);
        userQuery.setLimit(3);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                ParseQuery<Event> eventQuery = Event.getQuery();
                eventQuery.whereStartsWith(Event.TITLE, newText);
                eventQuery.orderByDescending(Event.PARTICIPANT);
                eventQuery.setLimit(3);
                List<Event> events = new ArrayList<>();
                try {
                    events = eventQuery.find();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                List<Search_item> items = new ArrayList<>();
                items.add(new Search_item(getString(R.string.search_subtitle_friend)));
                for (ParseUser user : users) {
                    items.add(new Search_item(user));
                }
                items.add(new Search_item(getString(R.string.search_subtitle_event)));
                for (Event event : events) {
                    items.add(new Search_item(event));
                }
                searchAdapter.setItems(items);
                searchAdapter.notifyDataSetChanged();
            }
        });
        return false;
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }
}
