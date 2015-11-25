package com.example.my8;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
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
 * Created by 이태경 on 2015-11-25.
 */
class MyStamp_item {
    int stampID;
    Bitmap image;

    int maxStamp=10;

    int getID(){return this.stampID;}
    Bitmap getImage(){return this.image;}

    public MyStamp_item(int stampID, Bitmap image){
        this.stampID=stampID;
        this.image=image;
    }
}
public class MyStampAdapter extends RecyclerView.Adapter<MyStampAdapter.ViewHolder>{
    Context context;
    List<MyStamp_item> items;
    View header;
    final int VIEW_TYPE_HEADER=0;
    final int VIEW_TYPE_ITEM=1;
    public MyStampAdapter(Context context,View header){
        this.context=context;
        items=new ArrayList<>();
        this.header = header;
    }
    public MyStampAdapter(Context context, View header, List<MyStamp_item> items) {
        this.context=context;
        this.items=items;
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
        if(isHeader(position)){

        }
        else{
            final MyStamp_item item = items.get(position-1);
            //holder.image.setImageBitmap(item.getImage());
            holder.stamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Stamp ID: " + item.getID(), Toast.LENGTH_SHORT).show();
                }
            });
        }


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
        ImageView header_profile_image;
        ImageView header_cover_image;
        TextView header_user_name;
        TextView header_stamp_count;
        ImageView stamp;

        public ViewHolder(View itemView, int viewtype) {
            super(itemView);
            if(viewtype == VIEW_TYPE_HEADER){
                header_profile_image = (ImageView)itemView.findViewById(R.id.ms_profile_image);
                header_cover_image = (ImageView)itemView.findViewById(R.id.ms_cover_image);
                header_user_name = (TextView)itemView.findViewById(R.id.ms_user_name);
                header_stamp_count = (TextView)itemView.findViewById(R.id.ms_stamp_count);
            }
            else if(viewtype == VIEW_TYPE_ITEM){
                stamp = (ImageView)itemView.findViewById(R.id.ms_stamp);
            }
        }
    }
}
