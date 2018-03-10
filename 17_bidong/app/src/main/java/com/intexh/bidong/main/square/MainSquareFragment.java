package com.intexh.bidong.main.square;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import com.intexh.bidong.R;

public class MainSquareFragment extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private MyAdapter myAdapter;
	private ImageView rightIcon;
	private Fragment currentFragment;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tabmain);
		Resources res = getResources();
		findViewById(R.id.image_title_back).setVisibility(View.GONE);
		rightIcon = (ImageView) findViewById(R.id.fragment_tabmain_right);
		ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);

		float unSelectSize = 16;
		float selectSize = unSelectSize * 1.2f;

		int selectColor = res.getColor(R.color.title_text_color_high);
		int unSelectColor = res.getColor(R.color.title_text_color);
		indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.WHITE, 3));
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
		viewPager.setOffscreenPageLimit(3);

		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());

		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getSupportFragmentManager()
		myAdapter = new MyAdapter(getChildFragmentManager());
		indicatorViewPager.setAdapter(myAdapter);

		rightIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentFragment = myAdapter.getCurrentFragment();
				if(currentFragment instanceof MainSquareOffServiceFragment){
//					((MainSquareOffServiceFragment) currentFragment).showPopupWindow(rightIcon);
				}
				if(currentFragment instanceof MainSquareTrendFragment){
					((MainSquareTrendFragment) currentFragment).showActionSheet();
				}
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				currentFragment = myAdapter.getCurrentFragment();
				if(position == 2){
					rightIcon.setVisibility(View.VISIBLE);
					rightIcon.setImageResource(R.drawable.icon_ban_camera);
				}
				else{
					rightIcon.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});
	}


	@Override
	protected void onResumeLazy() {
		super.onResumeLazy();
	}

	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();
	}

	@Override
	protected void onFragmentStopLazy() {
		super.onFragmentStopLazy();
	}

	@Override
	protected void onPauseLazy() {
		super.onPauseLazy();
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		currentFragment = myAdapter.getCurrentFragment();
		if(null != currentFragment){
			currentFragment.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top_text, container, false);
			}
			TextView textView = (TextView) convertView;
			String tabName= "";
			switch(position){
				case 0:
					tabName = "附近";
					break;
				case 1:
					tabName = "约Ta";
					break;
				case 2:
					tabName = "动态";
					break;
			}
			textView.setText(tabName);
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			Fragment fragment = null;
			switch(position){
				case 0:
					fragment = new MainSquareNearFragment();
					break;
				case 1:
					fragment = new MainSquareOffServiceFragment();
					break;
				case 2:
					fragment = new MainSquareTrendFragment();
					break;

			}
			return fragment;
		}
	}

}
