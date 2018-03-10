package com.intexh.bidong.trend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.utils.VolleyImageUtils;

public class PhotoBrowserActivity extends Activity {
	public static final String TOTAL = "total";
	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private MyPageAdapter adapter;
	private int count;

	public List<Bitmap> bmps = new ArrayList<Bitmap>();

	public int total;

	RelativeLayout photo_relativeLayout;
	TextView numRatioView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pubphotobrowser);

		Intent intent = getIntent();
		int id = intent.getIntExtra("ID", 0);
		total = TrendPublishActivity.paths.size();
		for (String path : TrendPublishActivity.paths) {
			Bitmap bmp = VolleyImageUtils.getScaledBitmap(this, path);
			bmps.add(bmp);
			initListViews(bmp);
		}

		photo_relativeLayout = (RelativeLayout)findViewById(R.id.photo_relativeLayout);
		photo_relativeLayout.setBackgroundColor(0x70000000);
		numRatioView = (TextView)findViewById(R.id.photo_count_tatio);
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.addOnPageChangeListener(pageChangeListener);

		adapter = new MyPageAdapter(listViews);// 构造adapter
		pager.setAdapter(adapter);// 设置适配器
		pager.setCurrentItem(id);

		Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		photo_bt_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				back();
			}
		});
		Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (listViews.size() == 0) {
					bmps.clear();
					back();
				} else {
					TrendPublishActivity.removeData(count);
					total--;
					if(total == 0){
						back();
					}else{
						bmps.remove(count);
						listViews.remove(count);
						pager.removeAllViews();
						adapter.setListViews(listViews);
						adapter.notifyDataSetChanged();
					}
				}
			}
		});
		Button photo_bt_enter = (Button) findViewById(R.id.photo_bt_enter);
		photo_bt_enter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				back();
			}
		});
	}

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		ImageView img = new ImageView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listViews.add(img);// 添加view
	}

	private void back(){
		Intent intent = new Intent();
		intent.putExtra(TOTAL, total);
		setResult(RESULT_OK, intent);
		finish();
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		public void onPageSelected(int arg0) {// 页面选择响应函数
			count = arg0;
			numRatioView.setText((count + 1) + "/" + total);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

		}

		public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
			// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
