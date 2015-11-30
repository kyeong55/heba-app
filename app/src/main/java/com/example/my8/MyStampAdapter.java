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

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-25.
 */
class MyStamp_item {
    Stamp stamp;

    String getID() { return this.stamp.getObjectId(); }
    ParseFile getImageFile() { return this.stamp.getPhotoFile(); }

    public MyStamp_item(Stamp stamp){
        this.stamp = stamp;
    }
}
public class MyStampAdapter extends RecyclerView.Adapter<MyStampAdapter.ViewHolder>{
    Context context;
    List<MyStamp_item> items;
    View header;
    final int VIEW_TYPE_HEADER=0;
    final int VIEW_TYPE_ITEM=1;
    public MyStampAdapter(Context context,View header){
        this.context = context;
        this.items = new ArrayList<>();
        this.header = header;
    }
    public MyStampAdapter(Context context, View header, List<MyStamp_item> items) {
        this.context = context;
        this.items = items;
        this.header = header;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER)
            return new ViewHolder(header,viewType);
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_stamp_card,parent,false);
        return new ViewHolder(v,viewType);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!isHeader(position)) {
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
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            params.height = metrics.widthPixels*3/7;
            holder.ms_card.setLayoutParams(params);
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
        return this.items.size() + 1;
    }
    public boolean isHeader(int position) {
        return position == 0;
    }
    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ParseImageView header_profile_image;
        ParseImageView header_cover_image;
        TextView header_user_name;
        TextView header_stamp_count;
        ParseImageView stamp;
        CardView ms_card;

        public ViewHolder(View itemView, int viewtype) {
            super(itemView);
            if(viewtype == VIEW_TYPE_HEADER){
                header_profile_image = (ParseImageView)itemView.findViewById(R.id.ms_profile_image);
                header_cover_image = (ParseImageView)itemView.findViewById(R.id.ms_cover_image);
                header_user_name = (TextView)itemView.findViewById(R.id.ms_user_name);
                header_stamp_count = (TextView)itemView.findViewById(R.id.ms_stamp_count);
            }
            else if(viewtype == VIEW_TYPE_ITEM){
                stamp = (ParseImageView)itemView.findViewById(R.id.ms_stamp);
                ms_card = (CardView)itemView.findViewById(R.id.ms_card_view);
            }
        }
    }
}
