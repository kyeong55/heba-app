package com.example.my8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter for playground
 */
class Playground_item {
    List<Playground_Stamp_item> playgroundStampItems;
    PgStampAdapter pgStampAdapter;
    Context context;
    ActionContract action;
    Event event;
    ParseUser user;

    public Playground_item(ViewGroup container, ActionContract action, Context context) {
        this.context = context;
        this.pgStampAdapter = new PgStampAdapter(context, event);
        this.playgroundStampItems = new ArrayList<>();

        this.action = action;
        this.event = action.getEvent();
        this.user = action.getUser();
    }

    public String getEventId() {
        return event.getObjectId();
    }

    public String getTitle() {
        return event.getTitle();
    }

    public String getNParticipant() {
        return event.getNParticipant() + "";
    }

    public List<Playground_Stamp_item> getPlaygroundStampItems() {
        return this.playgroundStampItems;
    }

    public void setPlaygroundStampItems(List<Playground_Stamp_item> playgroundStampItems) {
        this.playgroundStampItems = playgroundStampItems;
        this.pgStampAdapter.setItems(playgroundStampItems);
    }

    public PgStampAdapter getPgStampAdapter() {
        return this.pgStampAdapter;
    }

    public Date getUpdateTime() {
        return action.getUpdatedAt();
    }

    public void setIsAdding(boolean inAdding) {
        pgStampAdapter.setIsAdding(inAdding);
    }

    public boolean getIsAdding() {
        return pgStampAdapter.isAdding();
    }

    public void notifyDataSetChanged() {
        pgStampAdapter.notifyDataSetChanged();
    }

    public void setAddedAll(boolean addedAll) {
        pgStampAdapter.addedAll = addedAll;
    }

    public boolean getAddedAll() {
        return pgStampAdapter.addedAll;
    }

    public void add() {
        pgStampAdapter.add();
    }

    public Event getEvent() {
        return event;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getDescription() {
        switch (action.getAction()) {
            case 0:
                return "님이 위시리스트에 추가하였습니다.";
            case 1:
                return "님이 참여하였습니다.";
        }
        return "";
    }
}


public class PgAdapter extends RecyclerView.Adapter<PgAdapter.ViewHolder> {
    private ViewGroup container;
    private Context context;
    private List<Playground_item> items;
    private List<Event> checkList;

    final private int VIEW_TYPE_FOOTER=0;
    final private int VIEW_TYPE_ITEM=1;

    public boolean addedAll=false;
    private boolean inAdding=false;

    /* Constructors */
    public PgAdapter(ViewGroup container){
        this.container = container;
        this.context = container.getContext();
        items = new ArrayList<>();
        checkList = new ArrayList<>();
    }

    public void setItems(List<Playground_item> items) {
        this.items = items;
    }

    public void setCheckList(List<Event> checkList) {
        this.checkList = checkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer, parent, false);
        else
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new ViewHolder(v,viewType);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(isFooter(position)){
            if(addedAll) {
                holder.footer_progress_end.setVisibility(View.VISIBLE);
                holder.footer_progress_in.setVisibility(View.GONE);
            }
            else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        }
        else {
            // Fetch item from the ArrayList
            final Playground_item item = items.get(position);

            // Bind item with view
            holder.title.setText(item.getTitle());
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventInfoActivity.class);
                    intent.putExtra("event_id", item.getEventId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
                }
            });

            ParseUser user = item.getUser();


            if (user.getBoolean(User.EXIST_PROFILE)) {
                ParseImageView userProfile = (ParseImageView) holder.writer_photo;
                userProfile.setParseFile(user.getParseFile(User.PROFILE));
                userProfile.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        //nothing to do
                    }
                });
            }

            holder.writer.setText(user.getUsername());
            holder.description.setText(item.getDescription());
            holder.participate.setText(item.getNParticipant());
            holder.addWL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    List<ParseObject> wishlist = user.getList(User.WISHLIST);
                    List<ParseObject> donelist = user.getList(User.DONELIST);
                    Event event = item.getEvent();
                    String eventId = item.getEventId();
                    if (donelist != null && donelist.contains(eventId)) {
                        Toast.makeText(context, "이미 활동을 완료하였습니다", Toast.LENGTH_SHORT).show();
                    } else if (wishlist != null && wishlist.contains(eventId)) {
                        Toast.makeText(context, "이미 위시리스트에 추가되었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        ActionContract actionContract = new ActionContract(ParseUser.getCurrentUser(), ActionContract.WISHLIST, event);
                        actionContract.saveInBackground();

                        user.addUnique(User.WISHLIST, eventId);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(context, "위시리스트에 추가되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            // Attach image carousel to the view
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(layoutManager.HORIZONTAL);
            holder.stamps.setHasFixedSize(true);
            holder.stamps.setLayoutManager(layoutManager);
            holder.stamps.setAdapter(item.getPgStampAdapter());
            holder.stamps.addOnChildAttachStateChangeListener(new ChildAttachListener(layoutManager, item));
            refresh(item);
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size() + 1;
    }

    public boolean isFooter(int position) {
        return position == getItemCount()-1;
    }

    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
    }

    public boolean isAdding() {
        return inAdding;
    }

    public void setIsAdding(boolean inAdding){this.inAdding = inAdding;}

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

        List<ParseQuery<ParseUser>> userQueries = new ArrayList<ParseQuery<ParseUser>>();
        userQueries.add(userToQuery);
        userQueries.add(userFromQuery);

        ParseQuery<ParseUser> userQuery = ParseQuery.or(userQueries);

        ParseQuery<ActionContract> friendActionQuery = ActionContract.getQuery();
        friendActionQuery.whereMatchesQuery(ActionContract.USER, userQuery);

        ParseQuery<ActionContract> myActionQuery = ActionContract.getQuery();
        myActionQuery.whereEqualTo(ActionContract.USER, ParseUser.getCurrentUser());

        List<ParseQuery<ActionContract>> queries = new ArrayList<ParseQuery<ActionContract>>();
        queries.add(friendActionQuery);
        queries.add(myActionQuery);

        ParseQuery<ActionContract> query = ParseQuery.or(queries);
        query.orderByDescending("updatedAt");
        if (items.size() == 0) {
        } else {
            Playground_item oldestItem = items.get(items.size() - 1);
            query.whereLessThan("updatedAt", oldestItem.getUpdateTime());
        }
        query.setLimit(5);
        query.include(ActionContract.USER);
        query.include(ActionContract.EVENT);
        query.findInBackground(new FindCallback<ActionContract>() {
            @Override
            public void done(List<ActionContract> actions, ParseException e) {
                if (e == null) {
                    int pos = items.size();
                    for (ActionContract action : actions) {
                        if (!checkList.contains(action.getEvent())) {
                            checkList.add(action.getEvent());
                            Log.d("checkAdd", checkList + "");
                            //items.add(new Playground_item(container, action, context));
                        }
                        items.add(new Playground_item(container, action, context));
                    }
                    if(items.size()==pos){ // 더 이상 받아올게 없음
                        addedAll=true;
                        notifyItemChanged(pos);
                    }
                    else
                        notifyItemRangeInserted(pos, items.size() - pos);
                    inAdding=false;
                }
            }
        });
    }

    private class ChildAttachListener implements RecyclerView.OnChildAttachStateChangeListener {
        LinearLayoutManager llm;
        Playground_item item;

        public ChildAttachListener(LinearLayoutManager llm, Playground_item item){
            super();
            this.llm = llm;
            this.item = item;
        }

        @Override
        public void onChildViewAttachedToWindow(View view) {
            List<Playground_Stamp_item> items = item.getPlaygroundStampItems();
            if (items.size() - 2 <= llm.findLastVisibleItemPosition()) {
                if(!item.getIsAdding() && (items.size()>=5)&&(!item.getAddedAll()))
                    item.add();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {}
    }

    public void refresh(final Playground_item item) {
        item.setIsAdding(true);
        ParseQuery<Stamp> query = Stamp.getQuery();
        query.whereEqualTo("eventId", item.getEventId());
        query.orderByDescending("updatedAt");
        query.setLimit(6);
        query.findInBackground(new FindCallback<Stamp>() {
            @Override
            public void done(List<Stamp> stamps, ParseException e) {
                if (e == null) {
                    List<Playground_Stamp_item> newItems = new ArrayList<>();
                    for (Stamp stamp : stamps) {
                        newItems.add(new Playground_Stamp_item(stamp));
                    }
                    item.setPlaygroundStampItems(newItems);
                    item.notifyDataSetChanged();
                    item.setAddedAll(false);
                    item.setIsAdding(false);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ParseImageView writer_photo;
        TextView writer;
        TextView description;
        TextView participate;
        TextView addWL;
        RecyclerView stamps;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM) {
                title = (TextView) itemView.findViewById(R.id.pg_title);
                writer = (TextView) itemView.findViewById(R.id.pg_writer);
                description = (TextView) itemView.findViewById(R.id.pg_description);
                writer_photo = (ParseImageView) itemView.findViewById(R.id.pg_writer_photo);
                participate = (TextView) itemView.findViewById(R.id.pg_participate);
                addWL = (TextView) itemView.findViewById(R.id.pg_addWL);
                stamps = (RecyclerView) itemView.findViewById(R.id.pg_stamp_view);
            }
            else if(viewType == VIEW_TYPE_FOOTER) {
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.pg_progress);
                footer_progress_end.setText(R.string.pg_end_progress);
            }
        }
    }
}
