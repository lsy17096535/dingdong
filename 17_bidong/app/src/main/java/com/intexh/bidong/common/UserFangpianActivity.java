package com.intexh.bidong.common;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;

public class UserFangpianActivity extends BaseTitleActivity {

	@ViewInject(R.id.web_license_web)
	private WebView webView;
	@ViewInject(R.id.content_tv)
	private TextView content_tv;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_license);
		setTitleText("注意事项及防骗提醒");
//		webView.loadUrl("http://www.wappr.cn/fangpian");
	}

}
