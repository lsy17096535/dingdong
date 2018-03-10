package com.intexh.bidong.common;

import android.os.Bundle;
import android.webkit.WebView;

import com.lidroid.xutils.view.annotation.ViewInject;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;

public class OpporCommendActivity extends BaseTitleActivity {
	public static final String OPPOR_URL = "oppor_url";
	@ViewInject(R.id.web_oppor_web)
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_oppor);
		setTitleText("诚挚推荐");

		String url = getIntent().getStringExtra(OPPOR_URL);
		webView.loadUrl(url);
	}

}
