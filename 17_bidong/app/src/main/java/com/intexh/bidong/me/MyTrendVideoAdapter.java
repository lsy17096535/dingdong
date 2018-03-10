package com.intexh.bidong.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.me.MyTrendVideoAdapter.ViewHolder;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.widgets.MultiImageView;

public class MyTrendVideoAdapter extends RecyclerView.Adapter<ViewHolder> {
	private List<TrendVideoEntity> userList = null;
	private LayoutInflater mInflater = null;
	private View convertView = null;
	private int screenWidth = 0;
	private OnVideoListener videoListener;

	public void setDatas(List<TrendVideoEntity> datas){
		userList = datas;
		notifyDataSetChanged();
	}

	public MyTrendVideoAdapter(Context context, int screenWidth){
		super();
		mInflater = LayoutInflater.from(context);
		this.screenWidth = screenWidth;
	}

	public void setVideoListener(OnVideoListener videoListener){
		this.videoListener = videoListener;
	}

	public interface OnVideoListener{
		public void onClick(TrendVideoEntity entity);
	}


	class ViewHolder extends RecyclerView.ViewHolder{
		
		@ViewInject(R.id.txt_mytrend_commentcount)
		TextView commentcountTxt;
		@ViewInject(R.id.txt_mytrend_playcount)
		TextView playcountTxt;
		@ViewInject(R.id.txt_mytrend_day)
		TextView dayTxt;
		@ViewInject(R.id.txt_mytrend_month)
		TextView monthTxt;
		@ViewInject(R.id.txt_mytrend_videoaddr)
		TextView videoAddrTxt;
		@ViewInject(R.id.txt_mytrend_remark)
		TextView titleTxt;
		@ViewInject(R.id.viewstub_mytrend_video_image)
		ViewStub videoOrImgViewStub;

		MultiImageView multiImageView;
		ImageView playIconView;
		ImageView snapImageView;
		FrameLayout snapContainer;
	
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
		}
	}

	@Override
	public int getItemCount() {
		if(null != userList){
			return userList.size();
		}
		return 10;
	}

	@Override
	public int getItemViewType(int position){
		TrendVideoEntity entity = getItem(position);
		if(null != entity.getPhotos() && entity.getPhotos().length > 0){
			return 2;
		}
		else{
			return 1;
		}
	}

	protected TrendVideoEntity getItem(int index){
		if(null != userList){
			return userList.get(index);
		}
		return null;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int viewType) {
		convertView = mInflater.inflate(R.layout.listitem_mytrend, null);
		ViewHolder vh = new ViewHolder(convertView);
		if(1 == viewType){
			vh.videoOrImgViewStub.setLayoutResource(R.layout.viewstub_videopaly);
			vh.videoOrImgViewStub.inflate();
		}
		else{
			vh.videoOrImgViewStub.setLayoutResource(R.layout.viewstub_imgbody);
			vh.videoOrImgViewStub.inflate();
		}
		return vh;
	}
	
	public void onBindViewHolder(final ViewHolder vh, int index) {
		final TrendVideoEntity entity = getItem(index);
		boolean isVideo = true;
		if(null != entity.getPhotos() && entity.getPhotos().length > 0){
			isVideo = false;
		}
		int[] monthDay = DateUtil.getMonthDayArray(entity.getCreated_at());
		vh.monthTxt.setText(String.valueOf(monthDay[0]));
		vh.dayTxt.setText(String.valueOf(monthDay[1]));
		vh.commentcountTxt.setText(String.valueOf(entity.getComm_count()));
		vh.playcountTxt.setText(String.valueOf(entity.getPlay_count()));
		vh.titleTxt.setText(entity.getRemark());
		String videoAddr = (entity.getCity() != null ? entity.getCity() : "") +
				(entity.getDistrict() != null ? entity.getDistrict() : "") +
				(entity.getStreet() != null ? entity.getStreet() : "");
		if(null != entity.getUser().getCity() && null != entity.getCity()
				&& entity.getUser().getCity().equals(entity.getCity())){
			videoAddr = (entity.getDistrict() != null ? entity.getDistrict() : "") +
				(entity.getStreet() != null ? entity.getStreet() : "");
		}
		if("".equals(videoAddr.trim())){
			videoAddr = entity.getUser().getDistrict();
		}
		vh.videoAddrTxt.setText(videoAddr);

		if(isVideo){
			if(null == vh.snapContainer){
				vh.snapContainer = (FrameLayout)convertView.findViewById(R.id.layout_stub_video_snapshot);
				vh.playIconView = (ImageView)convertView.findViewById(R.id.image_stub_video_play);
				vh.snapImageView = (ImageView)convertView.findViewById(R.id.image_stub_video_snapshot);
				vh.snapContainer.setLayoutParams(new LinearLayout.LayoutParams(screenWidth*2/5, screenWidth*2/5));
				vh.snapImageView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth*2/5, screenWidth*2/5));
			}
			ImageUtils.loadSnapshotSmallImage(entity.getSnapshort(), vh.snapImageView, 0.8f);
			vh.playIconView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					videoListener.onClick(entity);
				}
			});
			vh.snapImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					videoListener.onClick(entity);
				}
			});
		}
		else {
			if(null == vh.multiImageView){
				vh.multiImageView = (MultiImageView) convertView.findViewById(R.id.multi_image_view);
			}
			vh.multiImageView.setList(Arrays.asList(entity.getPhotos()));
			vh.multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
				@Override
				public void onItemClick(ViewGroup parent, View view, int position) {
					videoListener.onClick(entity);
				}
			});
		}
	}
}
