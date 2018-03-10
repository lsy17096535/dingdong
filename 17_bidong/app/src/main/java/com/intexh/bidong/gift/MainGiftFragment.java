package com.intexh.bidong.gift;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import com.intexh.bidong.R;

public class MainGiftFragment extends LazyFragment{
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tabmain);
		findViewById(R.id.image_title_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getActivity().finish();
			}
		});
		Resources res = getResources();

		ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
		indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.WHITE, 2));

		float unSelectSize = 16;
		float selectSize = unSelectSize * 1.2f;

		int selectColor = res.getColor(R.color.title_text_color_high);
		int unSelectColor = res.getColor(R.color.title_text_color);
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

		viewPager.setOffscreenPageLimit(2);

		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());

		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getSupportFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
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

	private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
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
					tabName = "往";
					break;
				case 1:
					tabName = "来";
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
					fragment = new MainGiftOutFragment();
					break;
				case 1:
					fragment = new MainGiftInFragment();
					break;
			}
			return fragment;
		}
	}

}
