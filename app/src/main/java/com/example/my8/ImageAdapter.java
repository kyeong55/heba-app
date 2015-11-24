package com.example.my8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
 * Created by 이태경 on 2015-11-14.
 */
class Image_item {
    Bitmap image;
    int stamp_id;

    Bitmap getImage(){
        return this.image;
    }
    int getID(){
        return this.stamp_id;
    }

    Image_item(Bitmap image, int id){
        this.image=image;
        this.stamp_id=id;
    }
}
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    List<Image_item> items;
    public ImageAdapter(Context context){
        this.context=context;
        items=new ArrayList<>();
    }
    public ImageAdapter(Context context, List<Image_item> items) {
        this.context=context;
        this.items=items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.image,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Image_item item =items.get(position);
        //holder.image.setImageBitmap(item.getImage());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Stamp ID: "+item.getID(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void add(int stampID, Bitmap image){
        Image_item new_item = new Image_item(image, stampID);
        items.add(new_item);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image_view);
            cardview=(CardView)itemView.findViewById(R.id.image_card);
        }
    }
}
