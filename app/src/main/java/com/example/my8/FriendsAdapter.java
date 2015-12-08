package com.example.my8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-30.
 */
class Friends_item{
    private Friend friend;
    private UserInfo userInfo;

    public Friends_item(UserInfo userInfo) {
        this.friend = null;
        this.userInfo = userInfo;
    }

    public Friends_item(Friend friend, UserInfo userInfo) {
        this.friend = friend;
        this.userInfo = userInfo;
    }

    public ParseFile getProfile() {
        return userInfo.getProfile();
    }

    public String getName() {
        return userInfo.getName();
    }

    public int getStampCount() {
        return userInfo.getDonelist().size();
    }

    public String getId() {
        return userInfo.getUserId();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Friend getFriend() {
        return friend;
    }
}
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private Context context;
    private List<Friends_item> items;
    private int nRequest;

    final private int VIEW_TYPE_SUBTITLE_REQUEST=0;
    final private int VIEW_TYPE_SUBTITLE_SUGGEST=1;
    final private int VIEW_TYPE_ITEM_REQUEST=3;
    final private int VIEW_TYPE_ITEM_SUGGEST=4;
    final private int VIEW_TYPE_FOOTER=5;

    public boolean addedAll = false;
    private boolean inAdding = false;

    public FriendsAdapter(Context context){
        this.context = context;
        this.items = new ArrayList<>();
        this.nRequest = 0;
    }

    public void setItems(List<Friends_item> items) {
        this.items = items;
    }

    public void setNRequest(int nRequest) {
        this.nRequest = nRequest;
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
            final Friends_item item = items.get(getItemRequestPosition(position));

            //ParseImageView userProfile = (ParseImageView)holder.profile_image;
            //userProfile.setParseFile(item.getProfile());
            /*userProfile.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //nothing to do
                }
            });*/

            holder.user_name.setText(item.getName());
            //holder.stamp_count.setText(item.getStampCount());

            holder.friends_accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: accept friend request
                    Friend friend = item.getFriend();
                    friend.setState(Friend.APPROVED);

                    friend.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "친구 추가 되었습니다", Toast.LENGTH_SHORT).show();
                                holder.done_background.setVisibility(View.VISIBLE);
                                holder.done_init.setVisibility(View.INVISIBLE);
                                holder.done_accept.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

            holder.friends_reject_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //TODO: reject friend request
                    Friend friend = item.getFriend();
                    friend.setState(Friend.APPROVED);

                    friend.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                holder.done_background.setVisibility(View.VISIBLE);
                                holder.done_init.setVisibility(View.INVISIBLE);
                                holder.done_reject.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        } else if(viewType == VIEW_TYPE_ITEM_SUGGEST) {
            final Friends_item item = items.get(getItemSuggestPosition(position));

            ParseImageView userProfile = (ParseImageView)holder.profile_image;
            userProfile.setParseFile(item.getProfile());
            userProfile.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    //nothing to do
                }
            });

            holder.user_name.setText(item.getName());
            //holder.stamp_count.setText(item.getStampCount());

            holder.friends_add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: make request to friend
                    ParseUser I = ParseUser.getCurrentUser();
                    Friend friend = new Friend((UserInfo)I.getParseObject("userInfo"), I.getObjectId(), item.getId());

                    ParseACL friendACL = new ParseACL();
                    friendACL.setPublicReadAccess(true);
                    friendACL.setWriteAccess(item.getId(), true);
                    friendACL.setWriteAccess(ParseUser.getCurrentUser().getObjectId(), true);
                    friend.setACL(friendACL);

                    friend.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "친구 요청 되었습니다", Toast.LENGTH_SHORT).show();
                                holder.done_background.setVisibility(View.VISIBLE);
                                holder.done_init.setVisibility(View.INVISIBLE);
                                holder.done_add.setVisibility(View.VISIBLE);
                                holder.friends_add_button.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            });
        } else if(viewType == VIEW_TYPE_FOOTER) {
            if(addedAll) {
                holder.footer_progress_end.setVisibility(View.VISIBLE);
                holder.footer_progress_in.setVisibility(View.GONE);
            } else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public int getItemCount() {
        if(hasRequest())
            return items.size() + 3;
        else
            return items.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(hasRequest()){
            if(position == 0)
                return VIEW_TYPE_SUBTITLE_REQUEST;
            else if(position <= nRequest)
                return VIEW_TYPE_ITEM_REQUEST;
            else if(position == nRequest + 1)
                return VIEW_TYPE_SUBTITLE_SUGGEST;
            else if(position == getItemCount() - 1)
                return VIEW_TYPE_FOOTER;
            else
                return VIEW_TYPE_ITEM_SUGGEST;
        } else{
            if(position == 0)
                return VIEW_TYPE_SUBTITLE_SUGGEST;
            else if (position == getItemCount() - 1)
                return VIEW_TYPE_FOOTER;
            else
                return VIEW_TYPE_ITEM_SUGGEST;
        }
    }

    // True if there is at least one userInfo who requested to be friends
    public boolean hasRequest(){
        return nRequest>0;
    }

    public int getItemRequestPosition(int position){
        return position - 1;
    }

    public int getItemSuggestPosition(int position){
        if(hasRequest())
            return position - 2;
        else
            return position - 1;
    }

    public boolean isAdding() {
        return inAdding;
    }

    public void setIsAdding(boolean inAdding){this.inAdding = inAdding;}

    public void add(){
        inAdding = true;
        String I = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<Friend> friendToQuery = Friend.getQuery();
        friendToQuery.whereEqualTo(Friend.FROM_USER_ID, I);

        ParseQuery<Friend> friendFromQuery = Friend.getQuery();
        friendFromQuery.whereEqualTo(Friend.TO_USER_ID, I);

        /*
        List<ParseQuery<Friend>> queries = new ArrayList<ParseQuery<Friend>>();
        queries.add(friendToQuery);
        queries.add(friendFromQuery);

        ParseQuery<Friend> friendQuery = ParseQuery.or(queries);*/

        ParseQuery<UserInfo> query = UserInfo.getQuery();
        query.whereDoesNotMatchKeyInQuery(UserInfo.ID, Friend.FROM_USER_ID, friendToQuery);
        query.whereDoesNotMatchKeyInQuery(UserInfo.ID, Friend.TO_USER_ID, friendFromQuery);
        query.orderByDescending(UserInfo.ID);
        Log.d("itemsize", items.size()+"");
        Log.d("nRequest", nRequest + "");
        if (items.size() == nRequest) {
        } else {
            Friends_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan(UserInfo.ID, oldestItem.getId());
        }
        query.setLimit(5);
        query.findInBackground(new FindCallback<UserInfo>() {
            @Override
            public void done(List<UserInfo> userInfos, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    Log.d("pos", pos + "");
                    for (UserInfo userInfo : userInfos) {
                        items.add(new Friends_item(userInfo));
                        Log.d("username", userInfo.getName());
                    }
                    if (items.size() == pos) {
                        Log.d("addedAll", "done");
                        addedAll = true;
                        notifyItemChanged(getItemCount() - 1);
                    } else
                        notifyItemRangeInserted(pos, items.size() - pos);
                    inAdding = false;
                } else
                    Log.d("잡음", e.getMessage());
            }
        });
    }

    /*
    //TODO: test용
    public void add(){
        items_request.add(new Friends_item());
        items_suggest.add(new Friends_item());
        notifyDataSetChanged();
    }*/

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
