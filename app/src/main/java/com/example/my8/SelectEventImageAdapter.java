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
 * Created by HyunhoHa on 2015-11-24.
 */
class Select_Event_Image_item {
    Bitmap image;
    String image_title;
    int event_id;

    Bitmap getImage(){
        return this.image;
    }
    int getID(){
        return this.event_id;
    }
    String getImageTitle(){
        return this.image_title;
    }
    Select_Event_Image_item(Bitmap image, String title, int id){
        this.image=image;
        this.image_title = title;
        this.event_id =id;
    }
}
public class SelectEventImageAdapter extends RecyclerView.Adapter<SelectEventImageAdapter.ViewHolder> {
    Context context;
    List<Select_Event_Image_item> items;
    public SelectEventImageAdapter(Context context){
        this.context=context;
        items=new ArrayList<>();
    }
    public SelectEventImageAdapter(Context context, List<Select_Event_Image_item> items) {
        this.context=context;
        this.items=items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_with_title,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Select_Event_Image_item item =items.get(position);
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

    public void add(int stampID, String title, Bitmap image){
        Select_Event_Image_item new_item = new Select_Event_Image_item(image, title, stampID);
        items.add(new_item);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.image_with_title_view);
            text=(TextView)itemView.findViewById(R.id.image_with_title_text);
            cardview=(CardView)itemView.findViewById(R.id.image_with_title_card);
        }
    }
}
