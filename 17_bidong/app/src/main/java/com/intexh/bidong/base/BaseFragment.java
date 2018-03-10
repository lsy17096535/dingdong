package com.intexh.bidong.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {

	protected abstract void onBaseResume();
	protected abstract void onBasePause();
	
	protected void showToast(int content){
		Activity activity = getActivity();
		if(null != activity && activity instanceof BaseActivity){
			((BaseActivity)activity).showToast(content);
		}
	}
	
	protected void showToast(String content){
		Activity activity = getActivity();
		if(null != activity && activity instanceof BaseActivity){
			((BaseActivity)activity).showToast(content);
		}
	}
	
	protected void showLoading(){
		Activity activity = getActivity();
		if(null != activity && activity instanceof BaseActivity){
			((BaseActivity)activity).showLoading();
		}
	}
	
	protected void hideLoading(){
		Activity activity = getActivity();
		if(null != activity && activity instanceof BaseActivity){
			((BaseActivity)activity).hideLoading();
		}
	}
	
	@Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if(getActivity() == null){
        	return ;
        }
        if (visible) {
        	LogUtils.d( "onBaseResume()");
        	onBaseResume();
        }else{
        	LogUtils.d( "onBasePause()");
        	onBasePause();
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		String name = getClass().getSimpleName();
		MobclickAgent.onPageStart(name);
	}

	@Override
	public void onPause() {
		super.onPause();
		String name = getClass().getSimpleName();
		MobclickAgent.onPageEnd(name);
	}
}
