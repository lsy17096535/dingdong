/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intexh.bidong.easemob.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivityInterface;

import com.easemob.easeui.ui.EaseBaseActivity;

public class BaseActivity extends EaseBaseActivity implements BaseActivityInterface {

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
			mProgressDlg = new ProgressDialog(this);
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
	
	protected void hideSoftKeyboard(){
		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // umeng
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng
    }


}
