package com.example.my8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter for stamp image in playground
 */
class Playground_Stamp_item {
    Stamp stamp;
    ParseFile thumbnail;

    public Playground_Stamp_item(Stamp stamp) {
        this.stamp = stamp;
    }

    public Playground_Stamp_item(ParseFile thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getComment() {
        return stamp.getComment();
    }

    public ParseFile getImage() {
        return stamp.getThumbnail();
    }

    public Date getUpdateTime() {
        return stamp.getUpdatedAt();
    }

    public String getID () {
        return stamp.getObjectId();
    }
}

public class PgStampAdapter extends RecyclerView.Adapter<PgStampAdapter.ViewHolder> {
    private List<Playground_Stamp_item> items;
    private String eventId;
    private Context context;

    public boolean addedAll=false;
    private boolean inAdding=false;

    /* Constructors */
    public PgStampAdapter(Context context, String eventId){
        this.context = context;
        this.eventId = eventId;
        items = new ArrayList<>();
    }

    public void setItems(List<Playground_Stamp_item> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.image, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Fetch item from the ArrayList
        final Playground_Stamp_item item = items.get(position);

        // Bind item with view
        ParseImageView stampImage = (ParseImageView)holder.image;
        stampImage.setParseFile(item.getImage());
        stampImage.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                //nothing to do
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> stampIdList = getallStampObjectIdArrayList();
                Intent intent = new Intent(context, PgStampInfoActivity.class);
                String stampId = item.getID();
                int pos = stampIdList.indexOf(stampId);
                intent.putExtra("clicked_stamp_pos", pos);
                intent.putExtra("stamp_id_list", stampIdList);
                context.startActivity(intent);
            }
        });
    }

    private ArrayList<String> getallStampObjectIdArrayList() {
        ArrayList<String> stampIdList = new ArrayList<>();
        for (Playground_Stamp_item stamp : items) {
            stampIdList.add(stamp.getID());
        }
        return stampIdList;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public boolean isAdding() {
        return inAdding;
    }

    public void setIsAdding(boolean inAdding) {
        this.inAdding = inAdding;
    }

    public void add(){
        inAdding = true;
        ParseQuery<Stamp> query = Stamp.getQuery();
        query.whereEqualTo("eventId", eventId);
        query.orderByDescending("updatedAt");
        if (items.size() == 0) {
        } else {
            Playground_Stamp_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan("updatedAt", oldestItem.getUpdateTime());
        }
        query.setLimit(6);
        query.findInBackground(new FindCallback<Stamp>() {
            @Override
            public void done(List<Stamp> stamps, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    for (Stamp stamp : stamps) {
                        items.add(new Playground_Stamp_item(stamp));
                    }
                    if(items.size()==pos){ // 더 이상 받아올게 없음
                        addedAll=true;
                        notifyItemChanged(pos);
                    }
                    else
                        notifyItemRangeInserted(pos, items.size() - pos);
                    inAdding=false;
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
}
