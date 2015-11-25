package com.example.my8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HyunhoHa on 2015-11-26.
 */

class Stamp_Image {
    Bitmap image;
    String image_title;
    int event_id;

    Bitmap getImage(){
        return this.image;
    }
    int getID(){
        return this.event_id;
    }
    String getImageTitle(){
        return this.image_title;
    }
    Stamp_Image(Bitmap image, String title, int id){
        this.image=image;
        this.image_title = title;
        this.event_id =id;
    }
}
public class StampAdapter extends PagerAdapter {
    Context context;
    List<Stamp_Image> items;

    public StampAdapter(Context context){
        this.context=context;
        items=new ArrayList<>();
    }
    public StampAdapter(Context context, List<Stamp_Image> items) {
        this.context=context;
        this.items=items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        // TODO Auto-generated method stub

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.full_stamp,parent,false);

        //만들어진 View안에 있는 ImageView 객체 참조
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야 하는 것에 주의.
        TextView t = (TextView)v.findViewById(R.id.stamp_title);
        ImageView img= (ImageView)v.findViewById(R.id.stamp_image);
        t.setText(items.get(position).getImageTitle());

        //ImageView에 현재 position 번째에 해당하는 이미지를 보여주기 위한 작업
        //현재 position에 해당하는 이미지를 setting
        img.setImageBitmap(items.get(position).getImage());

        //ViewPager에 만들어 낸 View 추가
        parent.addView(v);

        //Image가 세팅된 View를 리턴
        return v;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View)object);

    }

    //instantiateItem() 메소드에서 리턴된 Objbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v==obj;
    }

    public void add(int stampID, String title, Bitmap image){
        Stamp_Image new_item = new Stamp_Image(image, title, stampID);
        items.add(new_item);
        this.notifyDataSetChanged();
    }
}
