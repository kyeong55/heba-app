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

import java.util.List;

/**
 * Adapter for cards in playground
 */
class StampQueryAdapter extends ParseQueryAdapter<Stamp> {

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
    }

    @Override
    public View getItemView(Stamp stamp, View v, ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.image, null);
        }

        super.getItemView(stamp, v, parent);

        ParseImageView stampImage = (ParseImageView) v.findViewById(R.id.image_view);
        ParseFile photoFile = stamp.getParseFile("photo");
        if (photoFile != null) {
            stampImage.setParseFile(photoFile);
            stampImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }
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
        stampQueryAdapter.getView(position, holder.image, parseParent);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo pass image_id

                Intent i = new Intent(context, Stamp_info.class);
                i.putExtra("currentstamppos", position);
                i.putExtra("stamplistid", getEventStampList(5));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
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
        ParseImageView image;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            image = (ParseImageView)v.findViewById(R.id.image_view);
            cardView = (CardView)v.findViewById(R.id.image_card);
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