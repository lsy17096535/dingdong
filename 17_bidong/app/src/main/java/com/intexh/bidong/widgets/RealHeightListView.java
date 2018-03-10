package com.intexh.bidong.widgets;
   
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
   
public class RealHeightListView extends ListView {   
    public RealHeightListView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
   
    public RealHeightListView(Context context) {   
        super(context);   
    }   
   
    public RealHeightListView(Context context, AttributeSet attrs, int defStyle) {   
        super(context, attrs, defStyle);   
    }   
   
    @Override   
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
   
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
                MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
    }   
   
} 