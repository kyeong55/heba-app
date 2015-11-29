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

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.search_menu, menu);
////        SearchManager searchManager =
////                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
////        searchView.setSearchableInfo(
////                searchManager.getSearchableInfo(getComponentName()));
////        searchView.setIconifiedByDefault(false);
////        searchView.requestFocus();
//        MenuItemCompat.expandActionView(menu.findItem(R.id.action_search));
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setOnCloseListener(new SearchView.OnCloseListener(){
//            @Override
//            public boolean onClose (){
//                finish();
//                return false;
//            }
//        });
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if(id == R.id.action_search) {
//            //TODO: search
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
