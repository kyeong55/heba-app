package com.example.my8;

import android.content.Context;
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
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-14.
 */
class Playground_item {
    PlaygroundStampAdapter playgroundStampAdapter;
    ImageAdapter Iadapter;
    Event event;

    public Playground_item(ViewGroup container, Event event) {
        this.playgroundStampAdapter = new PlaygroundStampAdapter(container.getContext(), container, event);
        this.Iadapter = new ImageAdapter(container.getContext());
        this.event = event;

        this.eventID=1;
        this.writer="누구야";
        this.participate=0;
        List<Playground_item> items=new ArrayList<>();
        for(int i=0;i<10;i++){
            addStamp(null,i);
        }
    }

    public String getTitle() {
        return event.getTitle();
    }

    public PlaygroundStampAdapter getPlaygroundStampAdapter() {
        return this.playgroundStampAdapter;
    }

    int eventID;
    String title;
    String writer;
    int participate;

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
    private ViewGroup container;
    private Context context;
    private List<Playground_item> items;
    final int VIEW_TYPE_FOOTER=0;
    final int VIEW_TYPE_ITEM=1;
    public boolean addedAll=false;
    private boolean inAdding=false;
    /* Constructors */
    public PgAdapter(ViewGroup container){
        this.container = container;
        this.context = container.getContext();
        items = new ArrayList<>();
    }
    public void setItems(List<Playground_item> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer, parent, false);
        else
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new ViewHolder(v,viewType);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(isFooter(position)){
            if(addedAll) {
                holder.footer_progress_end.setVisibility(View.VISIBLE);
                holder.footer_progress_in.setVisibility(View.GONE);
            }
            else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        }
        else {
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(layoutManager.HORIZONTAL);
            holder.stamps.setHasFixedSize(true);
            holder.stamps.setLayoutManager(layoutManager);
            holder.stamps.setAdapter(item.getPlaygroundStampAdapter());
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size() + 1;
    }
    public boolean isFooter(int position) {
        return position == getItemCount()-1;
    }
    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
    }
    public boolean isAdding() {
        return inAdding;
    }
    public void setIsAdding(boolean inAdding){this.inAdding = inAdding;}
    public void add(){
        inAdding = true;
        ParseQuery<Event> query = Event.getQuery();
        query.orderByDescending("updatedAt");
        query.setLimit(items.size() + 5);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    List<Playground_item> newItems = new ArrayList<>();
                    for (ParseObject event : events) {
                        newItems.add(new Playground_item(container, (Event) event));
                    }
                    items = newItems;
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
        TextView title;
        ParseImageView writer_photo;
        TextView writer;
        TextView participate;
        TextView addWL;
        RecyclerView stamps;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM) {
                title = (TextView) itemView.findViewById(R.id.pg_title);
                writer = (TextView) itemView.findViewById(R.id.pg_writer);
                writer_photo = (ParseImageView) itemView.findViewById(R.id.pg_writer_photo);
                participate = (TextView) itemView.findViewById(R.id.pg_participate);
                addWL = (TextView) itemView.findViewById(R.id.pg_addWL);
                stamps = (RecyclerView) itemView.findViewById(R.id.pg_stamp_view);
            }
            else if(viewType == VIEW_TYPE_FOOTER) {
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.pg_progress);
                footer_progress_end.setText(R.string.pg_end_progress);
            }
        }
    }
}
