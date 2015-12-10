package com.example.my8;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 이태경 on 2015-12-10.
 */
class Search_item {
    final int VIEW_TYPE_FRIEND=0;
    final int VIEW_TYPE_EVENT=1;
    final int VIEW_TYPE_SUBTITLE=2;

    private int viewType;
    private ParseUser user;
    private Event event;
    private String subtitle;

    public Search_item(ParseUser user){
        this.viewType = VIEW_TYPE_FRIEND;
        this.user = user;
    }
    public Search_item(Event event){
        this.viewType = VIEW_TYPE_EVENT;
        this.event = event;
    }
    public Search_item(String subtitle){
        this.viewType = VIEW_TYPE_SUBTITLE;
        this.subtitle = subtitle;
    }

    public int getViewType() {return viewType;}

    public ParseFile getUserProfile() {
        if (user.getBoolean(User.EXIST_PROFILE))
            return user.getParseFile(User.PROFILE);
        return null;
    }
    public String getUserName() { return user.getUsername(); }
    public String getUserStampCount() {
        if (user.getList(User.DONELIST) == null)
            return " "+0;
        return user.getList(User.DONELIST).size()+"";
    }
    public String getUserId() { return user.getObjectId(); }

    public String getEventTitle() {
        return event.getTitle();
    }
    public ParseFile getEventThumbnail(int idx) {
        return event.getThumbnail(idx);
    }
    public String getEventId() {
        return event.getObjectId();
    }
    public String getEventNParticipant() {
        return event.getNParticipant()+"";
    }
    public String getSubtitleText() { return subtitle; }
}
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private Context context;
    private List<Search_item> items;

    public final int VIEW_TYPE_FRIEND=0;
    public final int VIEW_TYPE_EVENT=1;
    final int VIEW_TYPE_SUBTITLE=2;

    private int imageHeight;
    private int numOfThumbNails;

    public SearchAdapter(Context context, int imageHeight, int numOfThumbNails){
        this.context = context;
        this.items = new ArrayList<>();
        this.imageHeight = imageHeight;
        this.numOfThumbNails = numOfThumbNails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == VIEW_TYPE_FRIEND)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_friend_card,parent,false);
        else if (viewType == VIEW_TYPE_EVENT)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_event_card, parent, false);
        else if (viewType == VIEW_TYPE_SUBTITLE)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_subtitle, parent, false);
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        final Search_item item = items.get(position);
        if (viewType == VIEW_TYPE_FRIEND){
            ParseFile profile = item.getUserProfile();
            if (profile != null) {
                holder.friend_profile.setParseFile(profile);
                holder.friend_profile.loadInBackground();
            }
            holder.friend_name.setText(item.getUserName());
            holder.friend_stamp_count.setText(item.getUserStampCount());
            holder.friend_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user_id", item.getUserId());
                    context.startActivity(intent);
                }
            });
        }
        else if (viewType == VIEW_TYPE_EVENT) {
            holder.event_title.setText(item.getEventTitle());
            holder.event_participate.setText(item.getEventNParticipant());
            ParseFile thumbnail1 = item.getEventThumbnail(1);
            ParseFile thumbnail2 = item.getEventThumbnail(2);

            if(thumbnail1 != null) {
                holder.event_image1.setParseFile(thumbnail1);
                holder.event_image1.loadInBackground();
            }
            if(thumbnail2 != null) {
                holder.event_image2.setParseFile(thumbnail2);
                holder.event_image2.loadInBackground();
            }
            if (numOfThumbNails > 2) {
                holder.event_image3.setVisibility(View.VISIBLE);
                ParseFile thumbnail3 = item.getEventThumbnail(3);
                if(thumbnail3 != null) {
                    holder.event_image3.setParseFile(thumbnail3);
                    holder.event_image3.loadInBackground();
                }
            }
            if (numOfThumbNails == 4) {
                holder.event_image4.setVisibility(View.VISIBLE);
                ParseFile thumbnail4 = item.getEventThumbnail(4);
                if(thumbnail4 != null) {
                    holder.event_image4.setParseFile(thumbnail4);
                    holder.event_image4.loadInBackground();
                }
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.event_image_layout.getLayoutParams();
            params.height = imageHeight;
            holder.event_image_layout.setLayoutParams(params);
            holder.event_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Event Info Activity
                    Intent intent = new Intent(context, EventInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event_id", item.getEventId());
                    context.startActivity(intent);
                }
            });
        }
        else if (viewType == VIEW_TYPE_SUBTITLE) {
            holder.subtitle.setText(item.getSubtitleText());
        }
    }

    public void setItems(List<Search_item> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public void add(Search_item item){
        items.add(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View friend_card;
        ParseImageView friend_profile;
        TextView friend_name;
        TextView friend_stamp_count;

        View event_card;
        TextView event_title;
        TextView event_participate;
        View event_image_layout;
        ParseImageView event_image1;
        ParseImageView event_image2;
        ParseImageView event_image3;
        ParseImageView event_image4;

        TextView subtitle;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_FRIEND){
                friend_card = itemView.findViewById(R.id.search_friend_card);
                friend_profile = (ParseImageView) itemView.findViewById(R.id.search_friend_profile_image);
                friend_name = (TextView) itemView.findViewById(R.id.search_friend_name);
                friend_stamp_count = (TextView) itemView.findViewById(R.id.search_friend_stamp_count);
            }
            else if (viewType == VIEW_TYPE_EVENT){
                event_card = itemView.findViewById(R.id.search_event_card);
                event_title = (TextView) itemView.findViewById(R.id.search_event_title);
                event_participate = (TextView) itemView.findViewById(R.id.search_event_participate);
                event_image_layout = itemView.findViewById(R.id.search_event_image_layout);
                event_image1 = (ParseImageView) itemView.findViewById(R.id.search_event_image_1);
                event_image2 = (ParseImageView) itemView.findViewById(R.id.search_event_image_2);
                event_image3 = (ParseImageView) itemView.findViewById(R.id.search_event_image_3);
                event_image4 = (ParseImageView) itemView.findViewById(R.id.search_event_image_4);
            }
            else if (viewType == VIEW_TYPE_SUBTITLE) {
                subtitle = (TextView) itemView.findViewById(R.id.friends_subtitle_text);
            }
        }
    }
}
