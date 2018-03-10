package com.intexh.bidong.trend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;
import java.util.ArrayList;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.constants.VideoInfo;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.VolleyImageUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class TrendPublishActivity extends BaseActivity {
	public static final int TAKE_PICTURE = 30001;
	public static final int BROWSER_PICTURE = 30002;
	public static final int REQUEST_SEL_IMAGE = 30003;

	public static final String IS_VIDEO = "is_video";

	private int max = 9;
	private ImageView backIcon;
	private EditText remarkEdit;
	private GridView noScrollgridview;
	private GridAdapter adapter;
	private TextView sendView;

	private FrameLayout videoContainer;
	private ImageView snapView;
	private FullscreenVideoLayout videoView;

	public static ArrayList<String> paths = new ArrayList<String>();
	public static ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();

	public static VideoInfo videoInfo;
	private boolean isVideo = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trend_publish);
		isVideo = getIntent().getBooleanExtra(IS_VIDEO, false);
		initView();
	}

	public void initView() {
		backIcon = (ImageView) findViewById(R.id.trend_public_back);
		sendView = (TextView) findViewById(R.id.activity_selectimg_send);
		remarkEdit = (EditText) findViewById(R.id.trend_public_edit);
		videoContainer = (FrameLayout) findViewById(R.id.layout_trend_videocontainer);
		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		if(isVideo){
			videoContainer.setVisibility(View.VISIBLE);
			noScrollgridview.setVisibility(View.GONE);
			videoView = (FullscreenVideoLayout) findViewById(R.id.video_trend_view);
			videoView.setActivity(this);
			videoView.hideBackView();
			videoView.hideMoreView();
			try{
				videoView.setVideoPath(videoInfo.videoPath);
				videoView.setShouldAutoplay(true);
			}catch (IOException e){}
		}
		else{
			videoContainer.setVisibility(View.GONE);
			noScrollgridview.setVisibility(View.VISIBLE);
			noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
			adapter = new GridAdapter(this);
			noScrollgridview.setAdapter(adapter);
			noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Log.e("frank","click paths=="+paths.size());
					if (arg2 == paths.size()) {
						new PopupWindows(TrendPublishActivity.this, noScrollgridview);
					} else {
						Intent intent = new Intent(TrendPublishActivity.this, PhotoBrowserActivity.class);
						intent.putExtra("ID", arg2);
						startActivityForResult(intent, BROWSER_PICTURE);
					}
				}
			});
		}

		backIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sendView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				publish();
			}
		});
	}

	public static void addPaths(ArrayList<String> tmpPaths,int ty){
		Log.e("frank","ty="+ty);
		for(String path : tmpPaths){
			addPath(path,0);
		}
	}

	public static void addPath(String tmpPath,int tt){
		Log.e("frank","tt="+tt);
		paths.add(tmpPath);
		bmps.add(VolleyImageUtils.getScaledBitmap(tmpPath, 80, 80));
		Log.e("frank","pathSize="+paths.size());
	}

	public static void removeData(int index){
		paths.remove(index);
		bmps.remove(index);
	}

	public static void clearData(){
		paths.clear();
		bmps.clear();
	}

	//删除临时目录
	public static void deleteTmpDir(){
		// 完成上传服务器后,删除临时目录
		TrendFileUtils.deleteDir();
	}

	private void publish(){
		String remark = remarkEdit.getText().toString();
		if(StringUtil.isEmptyString(remark)){
			showToast("请输入动态说明文字");
			return ;
		}
		if(isVideo){
			UploadUtil.uploadVideo(this, remark, videoInfo);
		}
		else{
			if(paths.isEmpty()){
				showToast("请添加动态照片");
			}
			UploadUtil.uploadPhotos(this, remark, paths);
		}

		showToast("动态正在上传，请稍后查看");
		setResult(RESULT_OK);
		finish();
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return (bmps.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.griditem_publish_image, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == bmps.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(bmps.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {
		public PopupWindows(Context mContext, View parent) {
			View view = View.inflate(mContext, R.layout.menuitem_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
			LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					capturePhoto();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(TrendPublishActivity.this, MultiImageSelectorActivity.class);
					// whether show camera
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
					// max select image amount
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, max);
					intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
					// default select images (support array list)
					intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
					startActivityForResult(intent, REQUEST_SEL_IMAGE);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	public void capturePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, TrendFileUtils.genTmpFile().getPath());
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_SEL_IMAGE:
					Log.e("frank","paths 22="+paths.size());
					clearData();
					Log.e("frank","paths 0="+paths.size());
					ArrayList<String> tmpPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					addPaths(tmpPaths,2);
					Log.e("frank","paths 1="+paths.size());
					adapter.notifyDataSetChanged();
					break;
				case TAKE_PICTURE:
					if (paths.size() < max && resultCode == -1) {
						addPath(data.getStringExtra(MediaStore.EXTRA_OUTPUT),1);
					}
					break;
				case BROWSER_PICTURE:
					adapter.notifyDataSetChanged();
					break;
			}
		}

	}

}
