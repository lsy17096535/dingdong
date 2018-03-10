package com.intexh.bidong.main.square;

import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.userentity.GiftItemEntity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GiftFragment extends BaseFragment {

	public interface OnGiftListener{
		public void onClickGift(GiftItemEntity gift);
	}
	
	private View mView = null;
	@ViewInject(R.id.recyleview_gift_grid)
	private RecyclerView gridView;
	private GiftGridAdapter giftAdapter = null;
	private List<GiftItemEntity> datas = null;
	private OnGiftListener mListener = null;
	private int showMode = GiftGridAdapter.SHOWMODE_NAME;
	
	public void setOnGiftListener(OnGiftListener listener){
		mListener = listener;
	}
	
	public void setDatas(List<GiftItemEntity> datas){
		this.datas = datas;
	}
	
	public void setShowMode(int mode){
		showMode = mode;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null == mView){
			mView = inflater.inflate(R.layout.fragment_gift, null);
			ViewUtils.inject(this, mView);
			giftAdapter = new GiftGridAdapter(getActivity());
			giftAdapter.setShowMode(showMode);
			giftAdapter.setDatas(datas);
			gridView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
			gridView.setAdapter(giftAdapter);
			gridView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener() {
				
				@Override
				public void onItemClick(View view, int position) {
					GiftItemEntity entity = datas.get(position);
					if(null != mListener){
						mListener.onClickGift(entity);
					}
				}
			}));
		}else{
			ViewGroup parent = (ViewGroup)mView.getParent();
			if(null != parent){
				parent.removeView(mView);
			}
		}
		return mView;
	}

	@Override
	protected void onBaseResume() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onBasePause() {

	}

}
