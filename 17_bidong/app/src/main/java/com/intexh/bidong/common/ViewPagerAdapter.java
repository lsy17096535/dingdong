package com.intexh.bidong.common;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter{

    private List<View> views;

    public ViewPagerAdapter(List<View> views){
        this.views = views;
        Log.d("ViewPager", String.valueOf(views.size()));
    }

    @Override
    public void destroyItem(ViewGroup group, int position, Object object) {
        group.removeView(views.get(position));
    }


    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup group, int position) {
        Log.d("ViewPager", "instantiateItem:" + String.valueOf(position));
        group.addView(views.get(position), 0);
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}