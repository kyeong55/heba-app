package com.example.my8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-30.
 */
class Friends_item{
    ParseFile getProfileImageFile() {
        return null;
    }
    String getName() {
        return "";
    }
    int getStampCount() {
        return 0;
    }
}
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    Context context;
    List<Friends_item> items;
    public FriendsAdapter(Context context){
        this.context = context;
        this.items = new ArrayList<>();
    }
    public FriendsAdapter(Context context, List<Friends_item> items) {
        this.context = context;
        this.items = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Friends_item item = items.get(position);
        //TODO; view user info
//        holder.user_name.setText(item.getName());
//        holder.profile_image.setParseFile(item.getProfileImageFile());
//        holder.profile_image.loadInBackground();
//        holder.stamp_count.setText(item.getStampCount());
        holder.friends_add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: adding friends
                Toast.makeText(context, "친구 추가 되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }
    public void add(){
        items.add(new Friends_item());
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        ParseImageView profile_image;
        TextView stamp_count;
        Button friends_add_button;
        public ViewHolder(View itemView) {
            super(itemView);
            user_name = (TextView)itemView.findViewById(R.id.friends_name);
            profile_image = (ParseImageView)itemView.findViewById(R.id.friends_profile_image);
            stamp_count = (TextView)itemView.findViewById(R.id.friends_stamp_count);
            friends_add_button = (Button)itemView.findViewById(R.id.friends_add_button);
        }
    }
}
