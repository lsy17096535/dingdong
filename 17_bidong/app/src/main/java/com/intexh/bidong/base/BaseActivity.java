package com.intexh.bidong.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import com.intexh.bidong.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public abstract class BaseActivity extends SwipeBackActivity implements com.intexh.bidong.base.BaseActivityInterface {
	
	public static final String FINISH_ALL = "FINISH_ALL";
	
	private Toast toast = null;
	private ProgressDialog mProgressDlg = null;
	
	public void showToast(int content){
		if(null != toast){
			toast.cancel();
			toast = null;
		}
		toast = Toast.makeText(this, content, Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void showToast(String content){
		if(null != toast){
			toast.cancel();
			toast = null;
		}
		toast = Toast.makeText(this, content, Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void showLoading(){
		if(null == mProgressDlg){
			mProgressDlg = new ProgressDialog(this,R.style.MyDialogStyle);
			mProgressDlg.setCancelable(false);
		}else{
			mProgressDlg.dismiss();
		}
		mProgressDlg.show();
		mProgressDlg.setContentView(R.layout.dlalog_loading);
	}
	
	public void hideLoading(){
		if(null != mProgressDlg){
			mProgressDlg.dismiss();
		}
	}
	
	public void hideSoftKeyboard(){
		View focusView = getCurrentFocus();
		if(null != focusView && null != focusView.getWindowToken()){
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		IntentFilter filter = new IntentFilter(FINISH_ALL);
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		hideSoftKeyboard();
		hideLoading();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		hideLoading();
		hideSoftKeyboard();
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		String name = getClass().getSimpleName();
		MobclickAgent.onPageEnd(name);
	}

	@Override
	protected void onResume() {
		super.onResume();
		String name = getClass().getSimpleName();
		MobclickAgent.onPageStart(name);
	}
}
