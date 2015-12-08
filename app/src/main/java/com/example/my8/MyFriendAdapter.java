package com.example.my8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-12-09.
 */
class MyFriends_item{
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
public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ViewHolder>{
    Context context;

    List<MyFriends_item> items;

    final int VIEW_TYPE_ITEM=0;
    final int VIEW_TYPE_FOOTER=1;

    public boolean addedAll = true;
    private boolean inAdding;
    public MyFriendAdapter(Context context){
        this.context = context;
        this.items = new ArrayList<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        if(viewType == VIEW_TYPE_ITEM)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_friend_card,parent,false);
        else if(viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer,parent,false);
        return new ViewHolder(v, viewType);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == VIEW_TYPE_ITEM) {
            final MyFriends_item item = items.get(position);
            //TODO: set info of user
            holder.user_name.setText("이태경");
            holder.stamp_count.setText("0");
//            holder.profile_image
        }
        else if(viewType == VIEW_TYPE_FOOTER) {
            if(addedAll) {
                holder.footer_progress_end.setVisibility(View.VISIBLE);
                holder.footer_progress_in.setVisibility(View.GONE);
            }
            else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public int getItemCount() {
        return items.size() + 1;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;
        else
            return VIEW_TYPE_ITEM;
    }

    //TODO: test용
    public void add(){
        items.add(new MyFriends_item());
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        ParseImageView profile_image;
        TextView stamp_count;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM){
                user_name = (TextView)itemView.findViewById(R.id.my_friends_name);
                profile_image = (ParseImageView)itemView.findViewById(R.id.my_friends_profile_image);
                stamp_count = (TextView)itemView.findViewById(R.id.my_friends_stamp_count);
            }
            else if(viewType == VIEW_TYPE_FOOTER) {
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.friends_progress);
                footer_progress_end.setText(R.string.friends_end_progress);
            }
        }
    }
}

