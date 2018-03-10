package com.intexh.bidong.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ProgressLayout extends LinearLayout {

	private int maxProgress = 100;
	private int value = 0;
	private Paint paint = null;
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		postInvalidate();
	}

	public ProgressLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(Color.parseColor("#ef5e6b"));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect(0, 0, getWidth()*value/maxProgress, getHeight());
		canvas.drawRect(rect, paint);
	}

	
}
