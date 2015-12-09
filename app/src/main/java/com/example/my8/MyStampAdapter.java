package com.example.my8;

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
 * Created by 이태경 on 2015-11-25.
 */
class MyStamp_item {
    Stamp stamp;

    String getID() { return this.stamp.getObjectId(); }
    ParseFile getImageFile() { return this.stamp.getThumbnail(); }

    public MyStamp_item(Stamp stamp){
        this.stamp = stamp;
    }

    public Date getUpdateTime() {
        return stamp.getUpdatedAt();
    }
}
public class MyStampAdapter extends RecyclerView.Adapter<MyStampAdapter.ViewHolder>{
    public boolean addedAll=false;
    private boolean inAdding=false;

    private Context context;
    private List<MyStamp_item> items;
    private View header;

    public final int VIEW_TYPE_HEADER=0;
    public final int VIEW_TYPE_ITEM=1;
    public final int VIEW_TYPE_FOOTER=2;

    private int imageHeight;

    public MyStampAdapter(Context context,View header, int imageHeight){
        this.context = context;
        this.items = new ArrayList<>();
        this.header = header;
        this.imageHeight = imageHeight;
    }

    public MyStampAdapter(Context context, View header, List<MyStamp_item> items) {
        this.context = context;
        this.items = items;
        this.header = header;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if(viewType == VIEW_TYPE_HEADER)
            v = header;
        else if (viewType == VIEW_TYPE_ITEM)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_stamp_card,parent,false);
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
            final MyStamp_item item = items.get(position-1);
            holder.stamp.setParseFile(item.getImageFile());
            holder.stamp.loadInBackground();
            holder.stamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MyStampInfoActivity.class);
                    String stampId = item.getID();
                    ArrayList<String> stampIdList = getStampObjectIdArrayList();
                    int pos = stampIdList.indexOf(stampId);
                    intent.putExtra("clicked_stamp_pos", pos);
                    intent.putExtra("stamp_id_list", stampIdList);
                    context.startActivity(intent);
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ms_card.getLayoutParams();
            params.height = imageHeight;
            holder.ms_card.setLayoutParams(params);
        }
        else if (viewType == VIEW_TYPE_HEADER) {
            ParseUser user = ParseUser.getCurrentUser();

            holder.header_user_name.setText(user.getUsername());
            if (user.getList(User.DONELIST) == null) {
                holder.header_stamp_count.setText("0");
            } else {
                holder.header_stamp_count.setText(user.getList(User.DONELIST).size() + "");
            }

            if (user.getBoolean(User.EXIST_PROFILE)) {
                holder.header_profile_image.setParseFile(user.getParseFile(User.PROFILE));
                holder.header_profile_image.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        //nothing to do
                    }
                });
            }

            if (user.getBoolean(User.EXIST_COVER)) {
                holder.header_cover_image.setParseFile(user.getParseFile(User.COVER));
                holder.header_cover_image.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        //nothing to do
                    }
                });
            }
        }
    }

    public void setItems(List<MyStamp_item> items) {
        this.items = items;
    }

    private ArrayList<String> getStampObjectIdArrayList() {
        ArrayList<String> stampIdList = new ArrayList<>();
        for (MyStamp_item stamp : items) {
            stampIdList.add(stamp.getID());
        }
        return stampIdList;
    }

    @Override
    public int getItemCount() {
        return this.items.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_HEADER;
        else if (position == getItemCount()-1)
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
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<Stamp> query = Stamp.getQuery();
        query.whereEqualTo(Stamp.USER, user);
        query.orderByDescending("updatedAt");
        if (items.size() == 0) {
        } else {
            MyStamp_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan("updatedAt", oldestItem.getUpdateTime());
        }
        query.setLimit(8);
        query.findInBackground(new FindCallback<Stamp>() {
            @Override
            public void done(List<Stamp> stamps, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    for (Stamp stamp : stamps) {
                        items.add(new MyStamp_item(stamp));
                        notifyItemInserted(items.size());
                    }
                    if (items.size() == pos) { // 더 이상 받아올게 없음
                        addedAll = true;
                        notifyItemChanged(pos + 1);
                    } //else
                        //notifyItemRangeInserted(pos + 1, items.size() - pos);
                }
                inAdding = false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ParseImageView header_profile_image;
        ParseImageView header_cover_image;
        TextView header_user_name;
        TextView header_stamp_count;
        ParseImageView stamp;
        CardView ms_card;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_HEADER){
                header_profile_image = (ParseImageView)itemView.findViewById(R.id.ms_profile_image);
                header_cover_image = (ParseImageView)itemView.findViewById(R.id.ms_cover_image);
                header_user_name = (TextView)itemView.findViewById(R.id.ms_user_name);
                header_stamp_count = (TextView)itemView.findViewById(R.id.ms_stamp_count);
            }
            else if(viewType == VIEW_TYPE_ITEM){
                stamp = (ParseImageView)itemView.findViewById(R.id.ms_stamp);
                ms_card = (CardView)itemView.findViewById(R.id.ms_card_view);
            }
            else if (viewType == VIEW_TYPE_FOOTER){
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.ms_progress);
                footer_progress_end.setText(R.string.ms_end_progress);
            }
        }
    }
}
