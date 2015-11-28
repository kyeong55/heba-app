package com.example.my8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-14.
 */
class Playground_item {
    int eventID;
    String title;
    String writer;
    int participate;

    ImageAdapter Iadapter;
    static final int maxStamp=10;

    /* Constructors */
    // For debugging
    public Playground_item(Context cont) {
        this.eventID=1;
        this.title="테스트 이벤트";
        this.writer="누구야";
        this.participate=0;
        this.Iadapter = new ImageAdapter(cont);
        List<Playground_item> items=new ArrayList<>();
        for(int i=0;i<10;i++){
            addStamp(null,i);
        }
    }
    public Playground_item(int eventID, String title, String writer, int participate, Context cont) {
        this.eventID=eventID;
        this.title=title;
        this.writer=writer;
        this.participate=participate;
        this.Iadapter = new ImageAdapter(cont);
    }

    /* Member functions */
    int getID() { return eventID; }
    String getTitle() { return title; }
    String getWriter() { return writer; }
    String getParticipate() { return ""+participate; }
    ImageAdapter getIadapter() { return Iadapter; }
    public void addStamp(Bitmap image, int stampID) {
        // Iadapter에 stamp 추가
        Iadapter.add(stampID, image);
        participate++;
    }
}


public class PgAdapter extends RecyclerView.Adapter<PgAdapter.ViewHolder> {
    Context context;
    List<Playground_item> items;
    int item_layout;

    /* Constructors */
    public PgAdapter(Context context){
        this.context=context;
        items = new ArrayList<>();
    }
    public PgAdapter(Context context, List<Playground_item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Fetch item from the ArrayList
        final Playground_item item = items.get(position);
        // Bind item with view
        holder.title.setText(item.getTitle());
        holder.writer.setText(item.getWriter());
        holder.participate.setText(item.getParticipate());
        holder.addWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "위시리스트에 추가되었습니다", Toast.LENGTH_SHORT).show();
                // TODO Add event to user's wishlist
            }
        });
        // Attach image carousel to the view
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        layoutManager.setOrientation(layoutManager.HORIZONTAL);
        holder.stamps.setHasFixedSize(true);
        holder.stamps.setLayoutManager(layoutManager);
        holder.stamps.setAdapter(item.getIadapter());
    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }
    public void add(Playground_item item){
        items.add(0,item);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView writer_photo;
        TextView writer;
        TextView participate;
        TextView addWL;
        RecyclerView stamps;

        public ViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.pg_title);
            writer=(TextView)itemView.findViewById(R.id.pg_writer);
            writer_photo=(ImageView)itemView.findViewById(R.id.pg_writer_photo);
            participate=(TextView)itemView.findViewById(R.id.pg_participate);
            addWL=(TextView)itemView.findViewById(R.id.pg_addWL);
            stamps=(RecyclerView)itemView.findViewById(R.id.pg_stamp_view);
        }
    }
}
