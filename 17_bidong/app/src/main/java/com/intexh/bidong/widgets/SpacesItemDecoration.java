package com.intexh.bidong.widgets;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.View;

public class SpacesItemDecoration extends ItemDecoration {
	private int space;
	private int numOfColumn;
	private int startOffset = 0;

	public void setStartOffset(int offset){
		this.startOffset = offset;
	}

	  public void setSpace(int space) {
		this.space = space;
	}

	public void setNumOfColumn(int numOfColumn) {
		this.numOfColumn = numOfColumn;
	}

	public SpacesItemDecoration(int space,int numOfColumn) {
	    this.space = space;
	    this.numOfColumn = numOfColumn;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildLayoutPosition(view);
		if(1 == numOfColumn){
			outRect.left = (int)(space*0.5);;
			outRect.right = (int)(space*0.5);;
		}
		else{
			if(position >= startOffset){
				position -= startOffset;
				if((position)%numOfColumn == 0){
					outRect.left = space;
					outRect.right = (int)(space*0.5);
				}else if((position+numOfColumn-1)%numOfColumn == 0){
					outRect.right = space;
					outRect.left = (int)(space*0.5);
				}else{
					outRect.left = (int)(space*0.5);
					outRect.right = (int)(space*0.5);
				}
			}
		}

		// Add top margin only for the first item to avoid double space between items
		if((1 == numOfColumn && position < numOfColumn)
				|| (2 == numOfColumn && position < numOfColumn)) {
			outRect.top = space;
		}
		else{
			outRect.top = 0;
		}
		outRect.bottom = space;
	}
}
