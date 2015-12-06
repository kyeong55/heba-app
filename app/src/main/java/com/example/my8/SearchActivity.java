package com.example.my8;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity
implements SearchView.OnQueryTextListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SearchView searchView = (SearchView)findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
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
        Toast.makeText(this, "Search: "+query, Toast.LENGTH_LONG).show();
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }
}
