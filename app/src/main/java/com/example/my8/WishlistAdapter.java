package com.example.my8;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
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
}
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{
    private Context context;
    private List<Wishlist_item> items;

    private final int VIEW_TYPE_FOOTER=0;
    private final int VIEW_TYPE_ITEM=1;

    public boolean addedAll=false;
    private boolean inAdding=false;

    public WishlistAdapter(Context context){
        this.context = context;
        this.items = new ArrayList<>();
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
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_card, parent, false);
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
            holder.thumbnail1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Magnify the stamp
                    //Intent intent = new Intent(context, ???.class);
                    //context.startActivity(intent);
                }
            });
            holder.thumbnail2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Magnify the stamp
                    //Intent intent = new Intent(context, ???.class);
                    //context.startActivity(intent);
                }
            });
            holder.thumbnail3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Magnify the stamp
                    //Intent intent = new Intent(context, ???.class);
                    //context.startActivity(intent);
                }
            });

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.wishCard.getLayoutParams();
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            params.height = metrics.widthPixels*1/3;
            holder.wishCard.setLayoutParams(params);
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
        List<String> wishlist = ParseUser.getCurrentUser().getList("wishlist");
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
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ParseImageView thumbnail1;
        ParseImageView thumbnail2;
        ParseImageView thumbnail3;
        View wishCard;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM) {
                title = (TextView) itemView.findViewById(R.id.wl_title);
                thumbnail1 = (ParseImageView) itemView.findViewById(R.id.wl_image_1);
                thumbnail2 = (ParseImageView) itemView.findViewById(R.id.wl_image_2);
                thumbnail3 = (ParseImageView) itemView.findViewById(R.id.wl_image_3);
                wishCard = (View) itemView.findViewById(R.id.wl_image_layout);
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
