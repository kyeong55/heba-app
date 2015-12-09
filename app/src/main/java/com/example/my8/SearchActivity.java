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
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
//        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
//        searchView.setQuery("", false);
//        searchView.setIconified(true);
        // TODO: search after submit
        Toast.makeText(this, "Search: "+query, Toast.LENGTH_LONG).show();
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO: auto search
        return false;
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.trans_activity_slide_right_in, R.anim.trans_activity_slide_right_out);
    }
}
