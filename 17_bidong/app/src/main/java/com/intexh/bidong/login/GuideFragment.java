package com.intexh.bidong.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GuideFragment extends BaseFragment {
	
	private int resId;
	private View mView = null;
	@ViewInject(R.id.image_guide_image)
	private ImageView imageView;
	@ViewInject(R.id.btn_guide_enter)
	private Button enterView;
	
	public void setResId(int resId) {
		this.resId = resId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null == mView){
			mView = inflater.inflate(R.layout.fragment_guide, null);
			ViewUtils.inject(this, mView);
			if(resId > 0){
				imageView.setImageResource(resId);
			}
			enterView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					getActivity().finish();
				}
			});
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
		// TODO Auto-generated method stub

	}

}
