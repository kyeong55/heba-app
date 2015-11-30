package com.example.my8;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Hy
 */
class Select_Event_Image_item {
    Event event;
    Stamp stamp;

    public Select_Event_Image_item(Event event, Stamp stamp) {
        this.event = event;
        this.stamp = stamp;
    }

    public String getTitle() {
        return event.getTitle();
    }

    public ParseFile getImage() {
        return stamp.getPhotoFile();
    }

    public String getEventId() {
        return event.getObjectId();
    }
}
public class SelectEventImageAdapter extends RecyclerView.Adapter<SelectEventImageAdapter.ViewHolder> {
    private Context context;
    private String imagePath;

    private List<Select_Event_Image_item> items;

    public SelectEventImageAdapter(Context context, String imagePath) {
        this.context = context;
        this.imagePath = imagePath;
        items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.image_with_title,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Select_Event_Image_item item = items.get(position);
        ParseImageView stampImage = (ParseImageView)holder.image;
        stampImage.setParseFile(item.getImage());
        stampImage.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                //nothing to do
            }
        });
        holder.text.setText(item.getTitle());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreateEventActivity = new Intent(context, Create_Event.class);
                toCreateEventActivity.putExtra("imagePath", imagePath);
                toCreateEventActivity.putExtra("eventId", items.get(position).getEventId());
                toCreateEventActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(toCreateEventActivity);
            }
        });
    }

    public void setItems(List<Select_Event_Image_item> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View cardView;
        ImageView image;
        TextView text;
        CardView card;

        public ViewHolder(View v) {
            super(v);
            cardView = v;
            image=(ParseImageView)v.findViewById(R.id.image_with_title_view);
            text=(TextView)v.findViewById(R.id.image_with_title_text);
            card=(CardView)v.findViewById(R.id.image_with_title_card);
        }
    }
}
