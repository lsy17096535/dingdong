package com.intexh.bidong.main.square;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.main.square.MainSquareTrendVideoAdapter.ViewHolder;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.widgets.MultiImageView;

//这是还是呢么ff
public class MainSquareTrendVideoAdapter extends RecyclerView.Adapter<ViewHolder> {

	private List<TrendVideoEntity> userList = null;
	private LayoutInflater mInflater = null;
	private View convertView = null;
	private int screenWidth = 0;
	private boolean isSelf = false;
	private OnVideoListener videoListener;
	private OnUserListener userListener;
	private OnCommentListener commentListener;

	public void setDatas(List<TrendVideoEntity> datas){
		userList = datas;
		notifyDataSetChanged();
	}

	public MainSquareTrendVideoAdapter(Context context, int screenWidth){
		super();
		mInflater = LayoutInflater.from(context);
		this.screenWidth = screenWidth;
	}

	public void setVideoListener(OnVideoListener videoListener){
		this.videoListener = videoListener;
	}

	public void setUserListener(OnUserListener userListener){
		this.userListener = userListener;
	}

	public void setCommentListener(OnCommentListener commentListener){
		this.commentListener = commentListener;
	}

	public interface OnVideoListener{
		public void onClick(TrendVideoEntity entity);
	}

	public interface OnUserListener{
		public void onClick(User user);
	}

	public interface OnCommentListener{
		public void onCommit(View v, String comment, String videoId);
	}

	class ViewHolder extends RecyclerView.ViewHolder{

		@ViewInject(R.id.txt_trendvideo_commentcount)
		TextView commentcountTxt;
		@ViewInject(R.id.txt_trendvideo_playcount)
		TextView playcountTxt;
		@ViewInject(R.id.txt_trendvideo_time)
		TextView timeTxt;
		@ViewInject(R.id.txt_trendvideo_distance)
		TextView distanceTxt;
		@ViewInject(R.id.txt_trendvideo_videoaddr)
		TextView videoAddrTxt;
		@ViewInject(R.id.txt_trendvideo_remark)
		TextView titleTxt;
		@ViewInject(R.id.viewstub_trend_video_image)
		ViewStub videoOrImgViewStub;
		@ViewInject(R.id.image_trendvideo_avatar)
		ImageView avatarView;
		@ViewInject(R.id.txt_trendvideo_username)
		TextView usernameView;
		@ViewInject(R.id.txt_trendvideo_age)
		TextView ageView;
		@ViewInject(R.id.txt_trendvideo_occup)
		TextView occupView;
		@ViewInject(R.id.txt_trendvideo_useraddr)
		TextView userAddrView;
		@ViewInject(R.id.txt_trendvideo_userdis)
		TextView userDistanceView;
		@ViewInject(R.id.layout_trendvideo_comment_oper)
		LinearLayout commentOperLayout;
		@ViewInject(R.id.layout_rendvideo_input)
		RelativeLayout inputLayout;
		@ViewInject(R.id.btn_rendvideo_send)
		Button sendButton;
		@ViewInject(R.id.edit_rendvideo_input)
		EditText commentEdit;

		MultiImageView multiImageView;
		ImageView playIconView;
		ImageView snapImageView;
		FrameLayout snapContainer;

		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
			if(isSelf){
				avatarView.setVisibility(View.GONE);
				usernameView.setText("");
				ageView.setText("");
				occupView.setText("");
				userAddrView.setText("");
				userDistanceView.setText("");
			}
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
		convertView = mInflater.inflate(R.layout.listitem_trendvideo, null);
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
		vh.commentcountTxt.setText(String.valueOf(entity.getComm_count()));
		vh.playcountTxt.setText(String.valueOf(entity.getPlay_count()));
		vh.titleTxt.setText(entity.getRemark());
		vh.timeTxt.setText(DateUtil.getTimeDiffDesc(entity.getCreated_at()));
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
		double[] vpoint = StringUtil.getLocPoint(entity.getLoc() != null? entity.getLoc() : entity.getUser().getLoc());
		if(null != vpoint){
			vh.distanceTxt.setText(StringUtil.getDistanceStr(vpoint)+"·");
		}

		//点击头像进入用户详情界面
		vh.avatarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userListener.onClick(entity.getUser());
			}
		});

		//点击评论按钮显示 评论输入区
//		vh.commentOperLayout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(vh.inputLayout.getVisibility() == View.GONE){
//					vh.inputLayout.setVisibility(View.VISIBLE);
//				}else {
//					vh.inputLayout.setVisibility(View.GONE);
//				}
//			}
//		});
		//点击发送按钮 提交评论内容
		vh.sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentListener.onCommit(v, vh.commentEdit.getText().toString(), entity.getId());
				vh.commentEdit.setText("");
			}
		});


		String ss = "";
		if(null != entity.getUser().getAlias()){
			ss += entity.getUser().getAlias();
		}

		if(!isSelf){
			ImageUtils.loadAvatarDefaultImage(entity.getUser().getAvatar(), vh.avatarView);
			vh.usernameView.setText(ss);

			int genderColorId = R.color.text_bg_color_red; //女色
			int genderFlag = R.drawable.icon_female1;
			if(2 == entity.getUser().getGender()){  //男
				genderColorId = R.color.text_bg_color_blue;
				genderFlag = R.drawable.icon_male1;
			}
			vh.ageView.setCompoundDrawablesWithIntrinsicBounds(genderFlag,0,0,0);
			vh.ageView.setBackgroundColor(PPStarApplication.getInstance().getResources().getColor(genderColorId));
			vh.ageView.setText(entity.getUser().getAge() + " ");

			vh.occupView.setText(" " + entity.getUser().getOccup() + " ");
			String userAddr = "";
			if(null != entity.getUser().getCity()){
				userAddr = entity.getUser().getCity();
			}
			if(null != entity.getUser().getDistrict()){
				userAddr += entity.getUser().getDistrict();
			}
			vh.userAddrView.setText(userAddr);
		}

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
