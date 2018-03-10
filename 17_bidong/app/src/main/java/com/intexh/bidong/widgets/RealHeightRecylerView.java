package com.intexh.bidong.widgets;
   
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
   
public class RealHeightRecylerView extends RecyclerView {   
    public RealHeightRecylerView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
   
    public RealHeightRecylerView(Context context) {   
        super(context);   
    }   
   
    public RealHeightRecylerView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
   
//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }
   
} 