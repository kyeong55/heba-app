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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 이태경 on 2015-11-30.
 */
class Wishlist_item{
    public Wishlist_item(){}
}
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{
    private ViewGroup container;
    Context context;
    List<Wishlist_item> items;
    final int VIEW_TYPE_FOOTER=0;
    final int VIEW_TYPE_ITEM=1;
    public boolean addedAll=false;
    private boolean inAdding=false;
    /* Constructors */
    public WishlistAdapter(ViewGroup container){
        this.container = container;
        this.context = container.getContext();
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
//        final MyStamp_item item = items.get(position-1);
//        holder.stamp.setParseFile(item.getImageFile());
//        holder.stamp.loadInBackground();
//        holder.stamp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, MyStampInfoActivity.class);
//                String stampId = item.getID();
//                ArrayList<String> stampIdList = getStampObjectIdArrayList();
//                int pos = stampIdList.indexOf(stampId);
//                intent.putExtra("clicked_stamp_pos", pos);
//                intent.putExtra("stamp_id_list", stampIdList);
//                context.startActivity(intent);
//            }
//        });
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ms_card.getLayoutParams();
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        params.height = metrics.widthPixels*3/7;
//        holder.ms_card.setLayoutParams(params);
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
//        inAdding = true;
        Wishlist_item newitem = new Wishlist_item();
        items.add(newitem);
        //TODO: add. (done 함수에서 마지막에 inAdding = false 넣고 새로 받아온거 길이비교해서 다받았으면 addedAll = true)
//        inAdding = false;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView participate;
        RecyclerView stamps;

        View footer_progress_in;
        TextView footer_progress_end;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == VIEW_TYPE_ITEM) {
                title = (TextView) itemView.findViewById(R.id.wl_title);
                participate = (TextView) itemView.findViewById(R.id.wl_participate);
//                stamps = (RecyclerView) itemView.findViewById(R.id.wl_stamp_view);
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
