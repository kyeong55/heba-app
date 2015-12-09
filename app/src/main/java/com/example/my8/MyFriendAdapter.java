package com.example.my8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

/**
 * Created by 이태경 on 2015-12-09.
 */
class MyFriends_item{
    private ParseUser user;

    public MyFriends_item(ParseUser user) {
        this.user = user;
    }

    public ParseFile getProfile() {
        if (user.getBoolean(User.EXIST_PROFILE))
            return user.getParseFile(User.PROFILE);
        return null;
    }

    public String getName() {
        return user.getUsername();
    }

    public String getStampCount() {
        if (user.getList(User.DONELIST) == null)
            return 0+"";
        return user.getList(User.DONELIST).size()+"";
    }
    public String getUserId(){
        return user.getObjectId();
    }
}
public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ViewHolder>{
    Context context;

    List<MyFriends_item> items;

    final int VIEW_TYPE_ITEM=0;
    final int VIEW_TYPE_FOOTER=1;

    public boolean addedAll = false;
    private boolean inAdding = false;

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
            holder.user_name.setText(item.getName());
            holder.stamp_count.setText(item.getStampCount());

            ParseFile profile = item.getProfile();
            if (profile != null) {
                ParseImageView userProfile = (ParseImageView) holder.profile_image;
                userProfile.setParseFile(item.getProfile());
                userProfile.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        //nothing to do
                    }
                });
            }

            holder.my_friend_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user_id", item.getUserId());
                    context.startActivity(intent);
//                    ((Activity)context).overridePendingTransition(R.anim.trans_activity_slide_left_in,R.anim.trans_activity_slide_left_out);
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
        return items.size() + 1;
    }

    public boolean isAdding() {
        return inAdding;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;
        else
            return VIEW_TYPE_ITEM;
    }

    public void add(){
        inAdding = true;
        String userId = ParseUser.getCurrentUser().getObjectId();

        ParseQuery<Friend> friendToQuery = Friend.getQuery();
        friendToQuery.whereEqualTo(Friend.STATE, Friend.APPROVED);
        friendToQuery.whereEqualTo(Friend.TO_USER_ID, userId);

        ParseQuery<ParseUser> userToQuery = ParseUser.getQuery();
        userToQuery.whereMatchesKeyInQuery(User.ID, Friend.FROM_USER_ID, friendToQuery);

        ParseQuery<Friend> friendFromQuery = Friend.getQuery();
        friendFromQuery.whereEqualTo(Friend.STATE, Friend.APPROVED);
        friendFromQuery.whereEqualTo(Friend.FROM_USER_ID, userId);

        ParseQuery<ParseUser> userFromQuery = ParseUser.getQuery();
        userFromQuery.whereMatchesKeyInQuery(User.ID, Friend.TO_USER_ID, friendFromQuery);

        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
        queries.add(userToQuery);
        queries.add(userFromQuery);

        ParseQuery<ParseUser> query = ParseQuery.or(queries);
        query.orderByAscending(User.NAME);
        if (items.size() != 0){
            MyFriends_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan(User.ID, oldestItem.getName());
        }
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    for (ParseUser user : users) {
                        items.add(new MyFriends_item(user));
                    }
                    if (items.size() == pos) {
                        addedAll = true;
                        notifyItemChanged(getItemCount() - 1);
                    } else
                        notifyItemRangeInserted(pos, items.size() - pos);
                    inAdding = false;
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name;
        ParseImageView profile_image;
        TextView stamp_count;
        View my_friend_card;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM){
                user_name = (TextView)itemView.findViewById(R.id.my_friends_name);
                profile_image = (ParseImageView)itemView.findViewById(R.id.my_friends_profile_image);
                stamp_count = (TextView)itemView.findViewById(R.id.my_friends_stamp_count);
                my_friend_card = itemView.findViewById(R.id.my_friend_card);
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

