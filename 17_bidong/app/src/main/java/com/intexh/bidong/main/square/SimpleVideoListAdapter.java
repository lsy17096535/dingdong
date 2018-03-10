package com.intexh.bidong.main.square;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.VideoItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.widgets.MultiImageView;

public class SimpleVideoListAdapter extends BaseAdapter {

	private List<VideoItemEntity> datas = null;
	private LayoutInflater mInflater = null;
	private Context context = null;
	private int screenWidth = 0;

	public interface OnItemListener{
		public void onClickItem(VideoItemEntity entity);
	}

	private OnItemListener mListener = null;

	public void setItemListener(OnItemListener mListener) {
		this.mListener = mListener;
	}


	public SimpleVideoListAdapter(Context context, int screenWidth){
		super();
		mInflater = LayoutInflater.from(context);
		this.context = context;
		//窗口的宽度
		this.screenWidth = screenWidth;
	}
	
	public void setDatas(List<VideoItemEntity> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(null != datas){
			return datas.size();
		}
		return 0;
	}

	@Override
	public VideoItemEntity getItem(int arg0) {
		if(null != datas){
			return datas.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		if(null != datas){
			return arg0;
		}
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		boolean isVideo = true;
		final VideoItemEntity entity = getItem(index);
		if(null != entity.getPhotos() && entity.getPhotos().length > 0){
			isVideo = false;
		}

		if(null == convertView){
			convertView = mInflater.inflate(R.layout.listitem_usertrend, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
			if(isVideo){
				holder.videoOrImgViewStub.setLayoutResource(R.layout.viewstub_videopaly);
				holder.videoOrImgViewStub.inflate();
				holder.snapContainer = (FrameLayout)convertView.findViewById(R.id.layout_stub_video_snapshot);
				holder.playIconView = (ImageView)convertView.findViewById(R.id.image_stub_video_play);
				holder.snapImageView = (ImageView)convertView.findViewById(R.id.image_stub_video_snapshot);
				holder.snapContainer.setLayoutParams(new LinearLayout.LayoutParams(screenWidth*2/5, screenWidth*2/5));
				holder.snapImageView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth*2/5, screenWidth*2/5));
				ImageUtils.loadSnapshotSmallImage(entity.getSnapshort(), holder.snapImageView, 0.8f);
				holder.playIconView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("TREND", "VIDEO CLICK");
						mListener.onClickItem(entity);
					}
				});
				holder.snapImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("TREND", "VIDEO CLICK");
						mListener.onClickItem(entity);
					}
				});
			}
			else {
				holder.videoOrImgViewStub.setLayoutResource(R.layout.viewstub_imgbody);
				holder.videoOrImgViewStub.inflate();
				holder.multiImageView = (MultiImageView) convertView.findViewById(R.id.multi_image_view);

				holder.multiImageView.setList(Arrays.asList(entity.getPhotos()));
				holder.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
					@Override
					public void onItemClick(ViewGroup parent, View view, int position) {
						mListener.onClickItem(entity);
					}
				});
			}
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.usernameTxt.setText(entity.getRemark());
		holder.commentCountTxt.setText(String.valueOf(entity.getComm_count()));
		holder.playCountTxt.setText(String.valueOf(entity.getPlay_count()));
		holder.timeView.setText(DateUtil.getTimeDiffDesc(entity.getCreated_at()));

		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.viewstub_usertrend_video_image)
		ViewStub videoOrImgViewStub;
		@ViewInject(R.id.txt_usertrend_remark)
		TextView usernameTxt;
		@ViewInject(R.id.txt_usertrend_commentcount)
		TextView commentCountTxt;
		@ViewInject(R.id.txt_usertrend_playcount)
		TextView playCountTxt;
		@ViewInject(R.id.txt_usertrend_time)
		TextView timeView;

		MultiImageView multiImageView;
		ImageView playIconView;
		ImageView snapImageView;
		FrameLayout snapContainer;
	}
	
}
