package com.example.my8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Adapter for wishlist
 */
class Wishlist_item{
    private Event event;

    public Wishlist_item(Event event){
        this.event = event;
    }

    public String getTitle() {
        return event.getTitle();
    }

    public ParseFile getThumbnail(int idx) {
        return event.getThumbnail(idx);
    }

    public String getEventId() {
        return event.getObjectId();
    }

    public String getNParticipant() {
        return event.getNParticipant()+"";
    }
}
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{
    private Context context;
    private List<Wishlist_item> items;

    private final int VIEW_TYPE_FOOTER=0;
    private final int VIEW_TYPE_ITEM=1;

    public boolean addedAll=false;
    private boolean inAdding=false;

    private int imageHeight;
    private int numOfThumbNails;

    public WishlistAdapter(Context context, int imageHeight, int numOfThumbNails){
        this.context = context;
        this.items = new ArrayList<>();
        this.imageHeight = imageHeight;
        this.numOfThumbNails = numOfThumbNails;
    }

    public void setItems(List<Wishlist_item> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == VIEW_TYPE_FOOTER)
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.playground_card_footer, parent, false);
        else
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_card_new, parent, false);
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(isFooter(position)) {
            if(addedAll) {
                holder.footer_progress_in.setVisibility(View.GONE);
                holder.footer_progress_end.setVisibility(View.VISIBLE);
            } else {
                holder.footer_progress_end.setVisibility(View.GONE);
                holder.footer_progress_in.setVisibility(View.VISIBLE);
            }
        } else {
            final Wishlist_item item = items.get(position);

            holder.title.setText(item.getTitle());
            holder.wl_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventInfoActivity.class);
                    intent.putExtra("event_id", item.getEventId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.trans_activity_slide_left_in, R.anim.trans_activity_slide_left_out);
                }
            });
//            ParseFile thumbnail1 = item.getThumbnail(1);
//            if (thumbnail1 != null) {
//                ParseImageView stampImage = (ParseImageView)holder.thumbnail1;
//                stampImage.setParseFile(thumbnail1);
//                stampImage.loadInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] data, ParseException e) {
//                        //nothing to do
//                    }
//                });
//            }
//            holder.thumbnail1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //TODO: Magnify the stamp
//                    //Intent intent = new Intent(context, ???.class);
//                    //context.startActivity(intent);
//                }
//            });
//
//            ParseFile thumbnail2 = item.getThumbnail(2);
//            if (thumbnail2 != null) {
//                ParseImageView stampImage = (ParseImageView)holder.thumbnail2;
//                stampImage.setParseFile(thumbnail2);
//                stampImage.loadInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] data, ParseException e) {
//                        //nothing to do
//                    }
//                });
//            }
//            holder.thumbnail2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //TODO: Magnify the stamp
//                    //Intent intent = new Intent(context, ???.class);
//                    //context.startActivity(intent);
//                }
//            });
//
//            ParseFile thumbnail3 = item.getThumbnail(3);
//            if (thumbnail3 != null) {
//                ParseImageView stampImage = (ParseImageView)holder.thumbnail3;
//                stampImage.setParseFile(thumbnail3);
//                stampImage.loadInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] data, ParseException e) {
//                        //nothing to do
//                    }
//                });
//            }
//            holder.thumbnail3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //TODO: Magnify the stamp
//                    //Intent intent = new Intent(context, ???.class);
//                    //context.startActivity(intent);
//                }
//            });

            ParseFile thumbnail1 = item.getThumbnail(1);
            ParseFile thumbnail2 = item.getThumbnail(2);

            if(thumbnail1 != null) {
                holder.thumbnail1.setParseFile(thumbnail1);
                holder.thumbnail1.loadInBackground();
            }
            if(thumbnail2 != null) {
                holder.thumbnail2.setParseFile(thumbnail2);
                holder.thumbnail2.loadInBackground();
            }
            if (numOfThumbNails > 2) {
                holder.thumbnail3.setVisibility(View.VISIBLE);
                ParseFile thumbnail3 = item.getThumbnail(3);
                if(thumbnail3 != null) {
                    holder.thumbnail3.setParseFile(thumbnail3);
                    holder.thumbnail3.loadInBackground();
                }
            }
            if (numOfThumbNails == 4) {
                holder.thumbnail4.setVisibility(View.VISIBLE);
                ParseFile thumbnail4 = item.getThumbnail(4);
                if(thumbnail4 != null) {
                    holder.thumbnail4.setParseFile(thumbnail4);
                    holder.thumbnail4.loadInBackground();
                }
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.thumbnail_layout.getLayoutParams();
            params.height = imageHeight;
            holder.thumbnail_layout.setLayoutParams(params);

//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.thumbnail_layout.getLayoutParams();
//            DisplayMetrics metrics = new DisplayMetrics();
//            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//            windowManager.getDefaultDisplay().getMetrics(metrics);
//            params.height = metrics.widthPixels*1/3;
//            holder.thumbnail_layout.setLayoutParams(params);
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

    public boolean isAdding() { return inAdding; }

    public void setIsAdding(boolean inAdding){this.inAdding = inAdding;}

    public void add(){
        inAdding = true;
        ParseUser user = ParseUser.getCurrentUser();
        List<String> wishlist = user.getList(User.WISHLIST);
        if (wishlist != null) {
            ParseQuery<Event> query = Event.getQuery();
            query.whereContainedIn("objectId", wishlist);
            query.orderByDescending("title");
            if (items.size() == 0) {
            } else {
                Wishlist_item oldestItem = items.get(items.size() - 1);
                query.whereLessThan("title", oldestItem.getTitle());
            }
            query.setLimit(5);
            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    if (e == null) {
                        int pos = items.size();
                        for (Event event : events) {
                            items.add(new Wishlist_item(event));
                        }
                        if (items.size() == pos) {
                            addedAll = true;
                            notifyItemChanged(pos);
                        } else {
                            notifyItemRangeInserted(pos, items.size() - pos);
                        }
                    }
                    inAdding = false;
                }
            });
        } else {
            addedAll = true;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ParseImageView thumbnail1;
        ParseImageView thumbnail2;
        ParseImageView thumbnail3;
        ParseImageView thumbnail4;
        View thumbnail_layout;
        View wl_card;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM) {
                title = (TextView) itemView.findViewById(R.id.wl_title);
                thumbnail1 = (ParseImageView) itemView.findViewById(R.id.wl_image_1);
                thumbnail2 = (ParseImageView) itemView.findViewById(R.id.wl_image_2);
                thumbnail3 = (ParseImageView) itemView.findViewById(R.id.wl_image_3);
                thumbnail4 = (ParseImageView) itemView.findViewById(R.id.wl_image_4);
                thumbnail_layout = (View) itemView.findViewById(R.id.wl_image_layout);
                wl_card = itemView.findViewById(R.id.wl_card);
            }
            else if(viewType == VIEW_TYPE_FOOTER) {
                footer_progress_in = itemView.findViewById(R.id.progress_in);
                footer_progress_end = (TextView) itemView.findViewById(R.id.progress_end);
                ((TextView)itemView.findViewById(R.id.progress_text)).setText(R.string.wl_progress);
                footer_progress_end.setText(R.string.wl_end_progress);
            }
        }
    }
}
