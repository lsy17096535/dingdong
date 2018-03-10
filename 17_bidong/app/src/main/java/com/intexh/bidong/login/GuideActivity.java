package com.intexh.bidong.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class GuideActivity extends BaseActivity {
	@ViewInject(R.id.pager_guide_pager)
	private ViewPager pager;
	@ViewInject(R.id.layout_guide_indicator)
	private LinearLayout indicatorContainer;

	private List<ImageView> indicators = new ArrayList<ImageView>();
	private boolean isGoLoginActivity;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_guide);
		ViewUtils.inject(this);
		pager.setAdapter(new GuidePageAdapter(getSupportFragmentManager()));
		pager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				updateIndicator(arg0);

			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if(arg0 == 4 && arg1 > 0.1){
					toLoginActivity();
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

		initIndicators();
		updateIndicator(0);
	}

	private void toLoginActivity(){
		if(isGoLoginActivity)return;
		Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		isGoLoginActivity = true;
	}
	
	private GuideFragment getGiftFragment(int index){
		GuideFragment fragment = new GuideFragment();
		switch(index){
			case 0:{
				fragment.setResId(R.drawable.start_1);
				break;
			}
			case 1:{
				fragment.setResId(R.drawable.start_2);
				break;
			}
			case 2:{
				fragment.setResId(R.drawable.start_3);
				break;
			}
			case 3:{
				fragment.setResId(R.drawable.start_4);
				break;
			}
			case 4:{
				fragment.setResId(R.drawable.start_5);
				break;
			}
			case 5:{
				break;
			}
		}
		return fragment;
	}

	private void initIndicators(){
		indicatorContainer.removeAllViews();
		int count = 5;
		for(int i=0;i<count;i++){
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, PtrLocalDisplay.dp2px(4), 0);
			imageView.setPadding(0, 0, PtrLocalDisplay.dp2px(4), 0);
			imageView.setImageResource(R.drawable.selector_giftindicator);
			indicatorContainer.addView(imageView, params);
			indicators.add(imageView);
		}
	}

	private void updateIndicator(int index){
		for(int i=0;i<indicators.size();i++){
			ImageView imageView = indicators.get(i);
			imageView.setSelected(index == i);
		}
	}
	
	private class GuidePageAdapter extends FragmentPagerAdapter{
		
		public GuidePageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return getGiftFragment(arg0);
		}

		@Override
		public int getCount() {
			return 6;
		}
		
	}
	
}
