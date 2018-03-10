package com.intexh.bidong.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends View {  
	private char[] l;  
    private SectionIndexer sectionIndexter = null;  
    private ListView list;  
    private TextView mDialogText;
    private int m_nItemHeight = 40;
    private int yOffset = 0;
	public SideBar(Context context) {  
	    super(context);  
	    init();  
	}
	
	public void setLayoutHeight(int fontSize){
		m_nItemHeight = fontSize;
	}
	
	public SideBar(Context context, AttributeSet attrs) {  
	    super(context, attrs);  
	    init();  
	}  
	
	private void init() {  
	    l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	            'T', 'U', 'V', 'W', 'X', 'Y', 'Z','#' };
	}
	
	public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	    super(context, attrs, defStyle); 
	    init();  
	}  
	
	public void setListView(ListView _list,SectionIndexer indexer) {  
	    list = _list;  
	    sectionIndexter = indexer;//(SectionIndexer) _list.getAdapter();  
	}  
	
	public void setTextView(TextView mDialogText) {  
		this.mDialogText = mDialogText;  
	}  
	
	public boolean onTouchEvent(MotionEvent event) {  
	    super.onTouchEvent(event);  
	    int i = (int) event.getY();  
	    i = i-yOffset;
	    int idx = i / m_nItemHeight;  
	    if (idx >= l.length) {  
	        idx = l.length - 1;  
	    } else if (idx < 0) {
	        idx = 0;  
	    }
	    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	    	mDialogText.setVisibility(View.VISIBLE);
	    	mDialogText.setText(""+l[idx]);
            int position = sectionIndexter.getPositionForSection(l[idx]);  
            if (position == -1) {  
                return true;  
            }  
            list.setSelection(position);  
	        }else{
	        	mDialogText.setVisibility(View.INVISIBLE);
	        }  
		 return true;  
	}  
	
    protected void onDraw(Canvas canvas) {  
    	float widthCenter = getMeasuredWidth() / 2;  
        float height = getMeasuredHeight();
        m_nItemHeight = (int)(height/(l.length));
        yOffset = (int)((height-m_nItemHeight*l.length)/2);
        Paint paint = new Paint();  
        paint.setColor(0xff595c61);
        paint.setTextSize(m_nItemHeight);
        paint.setTextAlign(Paint.Align.CENTER);  
        for (int i = 0; i < l.length; i++) {
            canvas.drawText(String.valueOf(l[i]), widthCenter, yOffset + m_nItemHeight + (i * m_nItemHeight), paint);  
        }  
        super.onDraw(canvas);  
    }  
}
