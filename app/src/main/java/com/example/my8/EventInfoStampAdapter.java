package com.example.my8;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 이태경 on 2015-12-08.
 */
class EventInfoStamp_item {
    Stamp stamp;

    String getID() { return this.stamp.getObjectId(); }
    ParseFile getImageFile() { return this.stamp.getThumbnail(); }

    public EventInfoStamp_item(Stamp stamp){
        this.stamp = stamp;
    }

    public Date getUpdateTime() {
        return stamp.getUpdatedAt();
    }
}
public class EventInfoStampAdapter extends RecyclerView.Adapter<EventInfoStampAdapter.ViewHolder>{
    public boolean addedAll=false;
    private boolean inAdding=false;

    private Context context;
    private List<EventInfoStamp_item> items;
    private String eventId;

    public final int VIEW_TYPE_ITEM=0;
    public final int VIEW_TYPE_FOOTER=1;

    private int imageHeight;
    private boolean firstStamp = false;

    public EventInfoStampAdapter(Context context, String eventId, int imageHeight){
        this.context = context;
        this.items = new ArrayList<>();
        this.eventId = eventId;
        this.imageHeight = imageHeight;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == VIEW_TYPE_ITEM)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_event_info_stamp_view_card,parent,false);
        else if (viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer, parent, false);
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_FOOTER){
            if(addedAll) {
                holder.footer_progress_end.setVisibility(View.VISIBLE);
                holder.footer_progress_in.setVisibility(View.GONE);
            }
            else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        }
        else if (viewType == VIEW_TYPE_ITEM) {
            final EventInfoStamp_item item = items.get(position);
            holder.eventStamp.setParseFile(item.getImageFile());
            holder.eventStamp.loadInBackground();
            holder.eventStamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, MyStampInfoActivity.class);
//                    String stampId = item.getID();
//                    ArrayList<String> stampIdList = getStampObjectIdArrayList();
//                    int pos = stampIdList.indexOf(stampId);
//                    intent.putExtra("clicked_stamp_pos", pos);
//                    intent.putExtra("stamp_id_list", stampIdList);
//                    context.startActivity(intent);
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.eventStamp.getLayoutParams();
            params.height = imageHeight;
            holder.eventStamp.setLayoutParams(params);
        }
    }

    public void setItems(List<EventInfoStamp_item> items) {
        this.items = items;
    }

    private ArrayList<String> getStampObjectIdArrayList() {
        ArrayList<String> stampIdList = new ArrayList<>();
        for (EventInfoStamp_item eventStamp : items) {
            stampIdList.add(eventStamp.getID());
        }
        return stampIdList;
    }

    @Override
    public int getItemCount() {
        return this.items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1)
            return VIEW_TYPE_FOOTER;
        else
            return VIEW_TYPE_ITEM;
    }

    public void setisAdding(boolean inAdding) {
        this.inAdding = inAdding;
    }
    public boolean isAdding() {
        return inAdding;
    }

    public void add(){
        inAdding = true;
        ParseQuery<Stamp> query = Stamp.getQuery();
        query.whereEqualTo("eventId", eventId);
        query.orderByDescending("updatedAt");
        if (items.size() == 0) {
            firstStamp = true;
        } else {
            EventInfoStamp_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan("updatedAt", oldestItem.getUpdateTime());
        }
        query.setLimit(8);
        query.findInBackground(new FindCallback<Stamp>() {
            @Override
            public void done(List<Stamp> stamps, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    for (Stamp eventStamp : stamps) {
                        items.add(new EventInfoStamp_item(eventStamp));
                        notifyItemInserted(items.size() - 1);
                        if(firstStamp){
                            firstStamp = false;
                            notifyDataSetChanged();
                        }
                    }
                    if (items.size() == pos) { // 더 이상 받아올게 없음
                        addedAll = true;
                        notifyItemChanged(pos);
                    } else{

                    }
//                        notifyItemRangeInserted(pos, items.size() - pos);
                }
                inAdding = false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ParseImageView eventStamp;
        View eventStampCard;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM){
                eventStamp = (ParseImageView)itemView.findViewById(R.id.event_info_stamp_view_image);
                eventStampCard = itemView.findViewById(R.id.event_info_stamp_view_card);
            }
            else if (viewType == VIEW_TYPE_FOOTER){
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.event_info_stamp_view_progress);
                footer_progress_end.setText(R.string.event_info_stamp_view_end_progress);
            }
        }
    }
}
