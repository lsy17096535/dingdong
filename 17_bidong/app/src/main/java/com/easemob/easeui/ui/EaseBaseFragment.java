package com.easemob.easeui.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;

import com.easemob.easeui.widget.EaseTitleBar;

public abstract class EaseBaseFragment extends Fragment{
    protected EaseTitleBar titleBar;
    protected InputMethodManager inputMethodManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        titleBar = (EaseTitleBar) getView().findViewById(R.id.title_bar);
        
        initView();
        setUpView();
    }
    
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
    
    /**
     * 显示标题栏
     */
    public void ShowTitleBar(){
        if(titleBar != null){
            titleBar.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * 隐藏标题栏
     */
    public void hideTitleBar(){
        if(titleBar != null){
            titleBar.setVisibility(View.GONE);
        }
    }
    
    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * 初始化控件
     */
    protected abstract void initView();
    
    /**
     * 设置属性，监听等
     */
    protected abstract void setUpView();
    
    
}
