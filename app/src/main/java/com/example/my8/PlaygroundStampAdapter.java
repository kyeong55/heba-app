package com.example.my8;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for cards in playground
 */
class StampQueryAdapter extends ParseQueryAdapter<Stamp> {

    Context cont;
    public StampQueryAdapter(Context context, final Event event) {
        super(context, new ParseQueryAdapter.QueryFactory<Stamp>() {
            public ParseQuery<Stamp> create() {
                // Here we can configure a ParseQuery to display
                // Do query
                ParseQuery<Stamp> query = Stamp.getQuery();
                //query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                query.whereEqualTo("event", event);
                query.orderByDescending("updatedAt");
                query.setLimit(5);
                return query;
            }
        });
        this.cont = context;
    }

    @Override
    public View getItemView(final Stamp stamp, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.image, null);
        }

        super.getItemView(stamp, v, parent);

        ParseImageView stampImage = (ParseImageView) v.findViewById(R.id.image_view);
        ParseFile photoFile = stamp.getPhotoFile();
        if (photoFile != null) {
            stampImage.setParseFile(photoFile);
            stampImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }

        CardView card = (CardView) v.findViewById(R.id.image_card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cont, PgStampInfoActivity.class);
                /*
                event.getObjectId();
                stamp.getObjectId();
                String stampId = item.getID();
                ArrayList<String> stampIdList = getStampObjectIdArrayList();
                int pos = stampIdList.indexOf(stampId);
                intent.putExtra("clicked_stamp_pos", pos);
                intent.putExtra("stamp_id_list", stampIdList);*/
                cont.startActivity(intent);
            }
        });
        return v;
    }
}

public class PlaygroundStampAdapter extends RecyclerView.Adapter<PlaygroundStampAdapter.ViewHolder> {
    private Context context;

    private StampQueryAdapter stampQueryAdapter;

    private ViewGroup parseParent;

    private PlaygroundStampAdapter playgroundStampAdapter = this;

    public PlaygroundStampAdapter(Context context, ViewGroup parentIn, final Event event) {
        this.context = context;
        parseParent = parentIn;
        stampQueryAdapter = new StampQueryAdapter(context, event);
        stampQueryAdapter.addOnQueryLoadListener(new OnQueryLoadListener());
        stampQueryAdapter.loadObjects();
    }

    @Override
    public PlaygroundStampAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        stampQueryAdapter.getView(position, holder.cardView, parseParent);
    }

    public int[] getEventStampList (int num){
        int stampIDList[] = new int[num];
        for (int i = 0; i < num; i++) {
            stampIDList[i] = i;
        }
        return stampIDList;
    }

    @Override
    public int getItemCount() {
        return stampQueryAdapter.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View cardView;
        ParseImageView image;
        CardView card;

        public ViewHolder(View v) {
            super(v);
            cardView = v;
            image = (ParseImageView)v.findViewById(R.id.image_view);
            card = (CardView)v.findViewById(R.id.image_card);
        }
    }

    public class OnQueryLoadListener implements ParseQueryAdapter.OnQueryLoadListener<Stamp> {

        @Override
        public void onLoading() {}

        @Override
        public void onLoaded(List<Stamp> objects, Exception e) {
            playgroundStampAdapter.notifyDataSetChanged();
        }
    }
}