package com.intexh.bidong.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.intexh.bidong.R;

public class FixImageView extends ImageView {

	private int mWidth = 0;
	private int mHeight = 0;
	private float scaleFactor = 0.6f;
	
//	private void setSaleFactor(float factor){
//		scaleFactor = factor;
//		mWidth = 0;
//		mHeight = 0;
//		measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
//	}
	
	public FixImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixImageView);
		scaleFactor = 1.0f;//a.getFloat(R.styleable.FixImageView_fiximage_ratio, 1.0f);
		a.recycle();
	}
	
	/** 
	   * 作用是返回一个默认的值，如果MeasureSpec没有强制限制的话则使用提供的大小.否则在允许范围内可任意指定大小 
	   * 第一个参数size为提供的默认大小，第二个参数为测量的大小 
	   */  
	  public static int getDefaultSize(int size, int measureSpec) {  
	      int result = size;  
	      int specMode = MeasureSpec.getMode(measureSpec);  
	      int specSize = MeasureSpec.getSize(measureSpec);  
	  
	      switch (specMode) {  
	      // Mode = UNSPECIFIED,AT_MOST时使用提供的默认大小  
	      case MeasureSpec.UNSPECIFIED:  
	          result = size;  
	          break;  
	      case MeasureSpec.AT_MOST:  
	      // Mode = EXACTLY时使用测量的大小   
	      case MeasureSpec.EXACTLY:  
	          result = specSize;  
	          break;  
	      }  
	      return result;  
	  }


	private Bitmap mBitmap = null;


	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = null;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		setMeasuredDimension(width,
		           (int)(width*scaleFactor));
//		if (width > height)
//			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
//		else
//			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
//		if ((0 == mWidth || 0 == mHeight) && 0 != width ) {
//			mWidth = (int)(width);
//			mHeight = (int)(width*scaleFactor);
//			setMeasuredDimension(mWidth, mHeight);
//		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		if(null == mBitmap){
			super.onDraw(canvas);
		}else{
			canvas.drawBitmap(mBitmap,new Rect(0,0,mBitmap.getWidth(),(int)(mBitmap.getWidth()*scaleFactor)),new Rect(0,0,getWidth(),(int)(getWidth()*scaleFactor)),null);
		}
	}
}
