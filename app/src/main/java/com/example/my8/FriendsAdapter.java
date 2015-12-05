package com.example.my8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    List<Friends_item> items_request;
    List<Friends_item> items_suggest;
    final int VIEW_TYPE_SUBTITLE_REQUEST=0;
    final int VIEW_TYPE_SUBTITLE_SUGGEST=1;
    final int VIEW_TYPE_ITEM_REQUEST=3;
    final int VIEW_TYPE_ITEM_SUGGEST=4;
    final int VIEW_TYPE_FOOTER=5;
    public boolean addedAll = true;
    private boolean inAdding;
    public FriendsAdapter(Context context){
        this.context = context;
        this.items_request = new ArrayList<>();
        this.items_suggest = new ArrayList<>();
    }
    public FriendsAdapter(Context context, List<Friends_item> items_request, List<Friends_item> items_suggest) {
        this.context = context;
        this.items_request = items_request;
        this.items_suggest = items_suggest;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=null;
        if((viewType == VIEW_TYPE_SUBTITLE_REQUEST)||(viewType == VIEW_TYPE_SUBTITLE_SUGGEST))
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_subtitle,parent,false);
        else if(viewType == VIEW_TYPE_ITEM_REQUEST)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_request_card,parent,false);
        else if(viewType == VIEW_TYPE_ITEM_SUGGEST)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_card,parent,false);
        else if(viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer,parent,false);
        return new ViewHolder(v, viewType);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == VIEW_TYPE_SUBTITLE_REQUEST) {}
        else if(viewType == VIEW_TYPE_SUBTITLE_SUGGEST) {}
        else if(viewType == VIEW_TYPE_ITEM_REQUEST) {
            final Friends_item item = items_request.get(getItemRequestPosition(position));
            //TODO: set info of requested user
            holder.user_name.setText("이태경");
            holder.stamp_count.setText("0");
            holder.friends_accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: accept friend request
                    Toast.makeText(context, "친구 추가 되었습니다", Toast.LENGTH_SHORT).show();
                    holder.done_background.setVisibility(View.VISIBLE);
                    holder.done_init.setVisibility(View.INVISIBLE);
                    holder.done_accept.setVisibility(View.VISIBLE);
                }
            });
            holder.friends_reject_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //TODO: reject friend request
                    holder.done_background.setVisibility(View.VISIBLE);
                    holder.done_init.setVisibility(View.INVISIBLE);
                    holder.done_reject.setVisibility(View.VISIBLE);
                }
            });
        }
        else if(viewType == VIEW_TYPE_ITEM_SUGGEST) {
            final Friends_item item = items_suggest.get(getItemSuggestPosition(position));
            //TODO: set info of user
            holder.user_name.setText("이태경");
            holder.stamp_count.setText("0");
            holder.friends_add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: make request to friend
                    Toast.makeText(context, "친구 요청 되었습니다", Toast.LENGTH_SHORT).show();
                    holder.done_background.setVisibility(View.VISIBLE);
                    holder.done_init.setVisibility(View.INVISIBLE);
                    holder.done_add.setVisibility(View.VISIBLE);
                    holder.friends_add_button.setVisibility(View.INVISIBLE);
                }
            });
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
        if(hasRequest())
            return items_request.size() + items_suggest.size() + 3;
        else
            return items_suggest.size() + 2;
    }
    @Override
    public int getItemViewType(int position) {
        if(hasRequest()){
            if(position == 0)
                return VIEW_TYPE_SUBTITLE_REQUEST;
            else if(position <= items_request.size())
                return VIEW_TYPE_ITEM_REQUEST;
            else if(position == items_request.size() + 1)
                return VIEW_TYPE_SUBTITLE_SUGGEST;
            else if(position == getItemCount() - 1)
                return VIEW_TYPE_FOOTER;
            else
                return VIEW_TYPE_ITEM_SUGGEST;
        }
        else{
            if(position == 0)
                return VIEW_TYPE_SUBTITLE_SUGGEST;
            else if (position == getItemCount() - 1)
                return VIEW_TYPE_FOOTER;
            else
                return VIEW_TYPE_ITEM_SUGGEST;
        }
    }

    // True if there is at least one user who requested to be friends
    public boolean hasRequest(){
        return items_request.size()>0;
    }
    public int getItemRequestPosition(int position){
        return position - 1;
    }
    public int getItemSuggestPosition(int position){
        if(hasRequest())
            return position - items_request.size() - 2;
        else
            return position - 1;
    }
//    public void done(ViewHolder holder,int position){
//        if(getItemViewType(position) == VIEW_TYPE_ITEM_REQUEST){
//            Log.w("Debugging", "pos: " + position + ", suggest pos: " + getItemRequestPosition(position));
//        }
//        else if(getItemViewType(position) == VIEW_TYPE_ITEM_SUGGEST){
//            Log.w("Debugging","pos: "+position+", suggest pos: "+getItemSuggestPosition(position));
//            items_suggest.remove(getItemSuggestPosition(position));
//            notifyItemRemoved(position);
//        }
//        notifyItemRangeRemoved(0,getItemCount());
//    }
    //TODO: test용
    public void add(){
        items_request.add(new Friends_item());
        items_suggest.add(new Friends_item());
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        ParseImageView profile_image;
        TextView stamp_count;
        Button friends_add_button;
        Button friends_accept_button;
        Button friends_reject_button;

        View footer_progress_in;
        TextView footer_progress_end;

        View done_background;
        View done_init;
        View done_add;
        View done_accept;
        View done_reject;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_SUBTITLE_REQUEST)
                ((TextView)itemView.findViewById(R.id.friends_subtitle_text)).setText(R.string.friends_subtitle_request);
            else if(viewType == VIEW_TYPE_SUBTITLE_SUGGEST)
                ((TextView)itemView.findViewById(R.id.friends_subtitle_text)).setText(R.string.friends_subtitle_suggest);
            else if(viewType == VIEW_TYPE_ITEM_REQUEST){
                user_name = (TextView)itemView.findViewById(R.id.friends_name);
                profile_image = (ParseImageView)itemView.findViewById(R.id.friends_profile_image);
                stamp_count = (TextView)itemView.findViewById(R.id.friends_stamp_count);
                friends_accept_button = (Button)itemView.findViewById(R.id.friends_request_accept_button);
                friends_reject_button = (Button)itemView.findViewById(R.id.friends_request_reject_button);
                done_background = itemView.findViewById(R.id.friends_done_background);
                done_init = itemView.findViewById(R.id.friends_done_init);
                done_accept = itemView.findViewById(R.id.friends_done_accept);
                done_reject = itemView.findViewById(R.id.friends_done_reject);
            }
            else if(viewType == VIEW_TYPE_ITEM_SUGGEST){
                user_name = (TextView)itemView.findViewById(R.id.friends_name);
                profile_image = (ParseImageView)itemView.findViewById(R.id.friends_profile_image);
                stamp_count = (TextView)itemView.findViewById(R.id.friends_stamp_count);
                friends_add_button = (Button)itemView.findViewById(R.id.friends_add_button);
                done_background = itemView.findViewById(R.id.friends_done_background);
                done_init = itemView.findViewById(R.id.friends_done_init);
                done_add = itemView.findViewById(R.id.friends_done_add);
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
