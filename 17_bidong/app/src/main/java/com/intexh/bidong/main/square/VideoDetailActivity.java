package com.intexh.bidong.main.square;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.github.rtoshiro.view.video.FullscreenVideoLayout.OnFullScreenVideoListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.common.CommonInputActivity;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.gift.GiftConfirmActivity;
import com.intexh.bidong.main.square.CommentListAdapter.OnCommentListener;
import com.intexh.bidong.me.DeleteVideoRequest;
import com.intexh.bidong.me.GetModelInfoRequest;
import com.intexh.bidong.me.MeFragment;
import com.intexh.bidong.me.model.ModelDetailActivity;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.CommentItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.HotVideoEntity;
import com.intexh.bidong.userentity.ModelInfoEntity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.utils.VolleyImageUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class VideoDetailActivity extends BaseActivity implements OnClickListener{
	public static final String VIDEOENTITY = "VIDEOENTITY";
	private static final int REQUEST_GIFT = 100;
	private static final int REQUEST_REPORT = REQUEST_GIFT + 1;
	@ViewInject(R.id.list_videodetail_list)
	private ListView videoListView;
	@ViewInject(R.id.btn_videodetail_send)
	private View sendView;
	@ViewInject(R.id.edit_videodetail_input)
	private EditText inputView;
	@ViewInject(R.id.image_videodetail_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.txt_videodetail_userinfo)
	private TextView usernameTxt;
	@ViewInject(R.id.txt_videodetail_remark)
	private TextView remarkTxt;
	@ViewInject(R.id.txt_videodetail_sign)
	private TextView signTxt;
	@ViewInject(R.id.btn_videodetail_contact)
	private Button contactView;
	@ViewInject(R.id.txt_videodetail_commentcount)
	private TextView commentCountView;
	@ViewInject(R.id.txt_videodetail_commentcount1)
	private TextView commentCount1View;
	@ViewInject(R.id.txt_videodetail_playcount)
	private TextView playCountView;
	@ViewInject(R.id.layout_videodetail_videocontainer)
	private View videoContainer;
	@ViewInject(R.id.video_videodetail_video)
	private FullscreenVideoLayout videoView;
	@ViewInject(R.id.txt_videodetail_addressinfo)
	private TextView addressInfoView;
	@ViewInject(R.id.pull_videodetail_refresh)
	private PtrClassicFrameLayout refreshLayout;
	@ViewInject(R.id.txt_videodetail_age)
	private TextView ageText;
	@ViewInject(R.id.txt_videodetail_height)
	private TextView heightView;
	@ViewInject(R.id.txt_videodetail_weight)
	private TextView weightView;
	@ViewInject(R.id.txt_videodetail_career)
	private TextView careerView;
	@ViewInject(R.id.image_videodetail_vip)
	private ImageView vipView;
	@ViewInject(R.id.layout_videodetail_userinfo)
	private View userinfoView;
	@ViewInject(R.id.image_videodetail_model)
	private ImageView modelView;
	private CommentListAdapter mAdapter = null;
	private List<CommentItemEntity> datas = new ArrayList<CommentItemEntity>();
	private HotVideoEntity videoEntity = null;
	private int videoHeight = 0;
	private Page page = new Page();
	private boolean isNeedClear = false;
	private boolean isNoMoreData = false;

	private int screenWidth = 0;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_videodetail);
		ViewUtils.inject(this);
		avatarView.setOnClickListener(this);

		//窗口的宽度
		screenWidth = VolleyImageUtils.getScreenDimension(this)[0];

		String ss = getIntent().getStringExtra(VIDEOENTITY);
		if (!StringUtil.isEmptyString(ss)) {
			videoEntity = GsonUtils.jsonToObj(ss, HotVideoEntity.class);
		}

		if(UserUtils.getUserid().equals(videoEntity.getUser().getId())){
			userinfoView.setVisibility(View.GONE);
		}
		else{
			initUserView();
		}

		mAdapter = new CommentListAdapter(this);
		mAdapter.setDatas(datas);
		mAdapter.setCommentListener(new OnCommentListener() {

			@Override
			public void onClickAvatar(final CommentItemEntity comment) {
				if (UserUtils.getUserid().equals(videoEntity.getUser().getId())
						&& !UserUtils.getUserid().equals(
								comment.getUser().getId())) {
					GetUserDetailRequest request = new GetUserDetailRequest();
					request.setUserid(comment.getUser().getId());
					request.setGender(comment.getUser().getGender());
					request.setNetworkListener(new CommonNetworkCallback<User>() {

						@Override
						public void onSuccess(User data) {
							hideLoading();
							data.setVideo(comment.getUser().getVideo());
							Intent intent = new Intent(VideoDetailActivity.this, UserDetailActivity.class);
							intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(data));
							startActivity(intent);
						}

						@Override
						public void onFailed(int code, HttpException error, String reason) {
							hideLoading();
							showToast(reason);
						}
					});
					showLoading();
					request.getDataFromServer();
				}
			}
		});
		videoListView.setAdapter(mAdapter);
		videoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CommentItemEntity entity = datas.get(arg2);
				inputView.setText("@" + entity.getUser().getAlias() + " ");
				inputView.requestFocus();
				inputView.setSelection(inputView.getText().length());
			}
		});
		refreshLayout.setPtrHandler(new PtrDefaultHandler2() {

			@Override
			public void onLoadMoreBegin(PtrFrameLayout frame) {
				if (isNoMoreData) {
					refreshLayout.refreshComplete();
				} else {
					getAllComments();
				}
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				isNeedClear = true;
				getAllComments();
			}

		});
		remarkTxt.setText(videoEntity.getRemark());
		sendView.setOnClickListener(this);

		contactView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!UserUtils.isVideoAudit()){
					UserDetailActivity.showRecordVideoAlert(VideoDetailActivity.this);
					return;
				}
				if (videoEntity.getUser().getId().equals(UserUtils.getUserid())) {
					showToast("不能与自己互动");
					return;
				}
				if(FriendsManager.getInstance().isValidFriend(videoEntity.getUser().getId())){
					openChat();
				}
				else{
					getAccountInfo();
				}
			}
		});

		videoView.setActivity(this);
		videoView.setShouldAutoplay(true);
		videoView.setOnFullScreenVideoListener(new OnFullScreenVideoListener() {
			@Override
			public void onScaleVideo(boolean isFull) {
				hideSoftKeyboard();
				if (isFull) {
					setVideoDispaly(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				} else {
					setVideoDispaly(LayoutParams.MATCH_PARENT, screenWidth*3/4);
				}
			}

			@Override
			public void onClickBack() {
				finish();
			}

			@Override
			public void onClickMore() {
				showMore();
			}
		});

		if (!UserUtils.getUserid().equals(videoEntity.getUser().getId())) {
			videoView.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					runOnUiThread(new Runnable() {
						public void run() {
							UpdatePlayCountRequest request = new UpdatePlayCountRequest();
							request.setUserid(videoEntity.getUser().getId());
							request.setId(videoEntity.getId());
							request.setViewerid(UserUtils.getUserid());
							request.setNetworkListener(new CommonNetworkCallback<String>() {
								@Override
								public void onSuccess(String data) {}
								@Override
								public void onFailed(int code, HttpException error, String reason) {}
							});
							request.getDataFromServer();
						}
					});
				}
			});
		}else{
			userinfoView.setVisibility(View.GONE);
		}

		setVideoDispaly(LayoutParams.MATCH_PARENT, screenWidth*3/4);
		setVideoURI(videoEntity.getVideo());

		commentCountView.setText(String.valueOf(videoEntity.getComm_count()));
		commentCount1View.setText(String.valueOf(videoEntity.getComm_count()));
		playCountView.setText(String.valueOf(videoEntity.getPlay_count()));
		getAllComments();
		getAccount();
	}

	private void initUserView(){
		ImageUtils.loadAvatarDefaultImage(videoEntity.getUser().getAvatar(), avatarView);
		String username = videoEntity.getUser().getAlias();
		if (null == username) {
			username = "";
		}
		usernameTxt.setText(username);
		String ss = " ";
		if (null != videoEntity.getUser().getHeight()) {
			ss += videoEntity.getUser().getHeight();
		}
		ss += "cm ";
		heightView.setText(ss);
		ss = " ";
		if (null != videoEntity.getUser().getWeight()) {
			ss += videoEntity.getUser().getWeight();
		}
		ss += "kg ";
		weightView.setText(ss);

		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == videoEntity.getUser().getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;
		}
		ageText.setBackgroundColor(getResources().getColor(genderColorId));
		ageText.setText(getString(genderFlag) + videoEntity.getUser().getAge() + " ");
		signTxt.setText(videoEntity.getUser().getSignature());
		ss = "";
		if (null != videoEntity.getUser().getCity()) {
			ss += videoEntity.getUser().getCity();
		}
		if (null != videoEntity.getUser().getDistrict()) {
			ss += videoEntity.getUser().getDistrict();
		}
		addressInfoView.setText(ss);
		ss = " ";
		if (null != videoEntity.getUser().getOccup()) {
			ss += videoEntity.getUser().getOccup() + " ";
			careerView.setText(ss);
		}else{
			careerView.setText("");
		}

		if (videoEntity.getUser().getGender() == 1) {
			contactView.setText("与她\n互动");
		} else {
			contactView.setText("与他\n互动");
		}
	}

	private void setVideoURI(String orgUri){
		try {
			String playUrl = PPStarApplication.getInstance().getProxyUrl(orgUri, this);
			videoView.setVideoPath(playUrl);
		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	private void setVideoDispaly(int width, int height){
		LayoutParams params = (LayoutParams) videoContainer.getLayoutParams();
		params = new LayoutParams(width, height);
		videoContainer.setLayoutParams(params);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		videoView.destroy();
	}
	
	private void showMore(){
		if(UserUtils.getUserid().equals(videoEntity.getUser().getId())){
			ActionSheet
			.createBuilder(this, getSupportFragmentManager())
			.setCancelButtonTitle(R.string.common_cancel)
			.setOtherButtonTitles("删除视频")
			.setListener(new ActionSheetListener() {
				@Override
				public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}

				@Override
				public void onOtherButtonClick(ActionSheet actionSheet, int index) {
					switch(index){
						case 0:{
							DeleteVideoRequest request = new DeleteVideoRequest();
							request.setVideoid(videoEntity.getId());
							request.setUserid(UserUtils.getUserid());
							request.setNetworkListener(new CommonNetworkCallback<String>() {

								@Override
								public void onSuccess(String data) {
									hideLoading();
									showToast("视频删除成功");
									setResult(RESULT_OK);
									finish();
								}

								@Override
								public void onFailed(int code, HttpException error,
										String reason) {
									hideLoading();
									showToast(reason);
								}
							});
							showLoading();
							request.getDataFromServer();
							break;
						}
					}
				}
				
			}).show();
		}else{
			ActionSheet
			.createBuilder(this, getSupportFragmentManager())
			.setCancelButtonTitle(R.string.common_cancel)
			.setOtherButtonTitles("举报视频")
			.setListener(new ActionSheetListener() {
				@Override
				public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}

				@Override
				public void onOtherButtonClick(ActionSheet actionSheet, int index) {
					switch(index){
						case 0:{
							Intent intent = new Intent(VideoDetailActivity.this,CommonInputActivity.class);
							intent.putExtra(CommonInputActivity.MAX_LENGTH, 64);
							intent.putExtra(CommonInputActivity.TIPS, "举报内容");
							intent.putExtra(CommonInputActivity.TITLE, "举报内容");
							intent.putExtra(CommonInputActivity.VALUE, "");
							startActivityForResult(intent, REQUEST_REPORT);
							break;
						}
					}
				}
				
			}).show();
		}
	}

	private void getAccount() {
		GetUserDetailRequest request = new GetUserDetailRequest();
		request.setUserid(videoEntity.getUser().getId());
		request.setGender(videoEntity.getUser().getGender());
		request.setNetworkListener(new CommonNetworkCallback<User>() {

			@Override
			public void onSuccess(User data) {
				User user = videoEntity.getUser();
				data.setVideo(user.getVideo());
				videoEntity.setUser(data);
				if(data.is_acting()){
					modelView.setVisibility(View.VISIBLE);
				}
				hideLoading();
				vipView.setImageResource(MeFragment.getLevelResId(data.getLevel(),data.getGender()));
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	private void openChat(){
		Intent intent = new Intent(VideoDetailActivity.this, ChatActivity.class);
		intent.putExtra(Constant.EXTRA_USER_ID, UserUtils.getHXUserid(videoEntity.getUser().getId()));
		startActivity(intent);
	}

	private void getAccountInfo() {
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				Intent intent = new Intent(VideoDetailActivity.this, GiftActivity.class);
				intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
				intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
				if(1 == videoEntity.getUser().getGender()){
					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
				}else{
					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_male_tips);
				}
				startActivityForResult(intent, REQUEST_GIFT);
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	private void getAllComments() {
		GetCommentsRequest request = new GetCommentsRequest();
		request.setVideoid(videoEntity.getId());
		if (isNeedClear) {
			request.setPage(new Page());
		} else {
			request.setPage(page);
		}
		request.setNetworkListener(new CommonNetworkCallback<CommentItemEntity[]>() {

			@Override
			public void onSuccess(CommentItemEntity[] data) {
				hideLoading();
				refreshLayout.refreshComplete();
				if (isNeedClear) {
					datas.clear();
					isNeedClear = false;
					isNoMoreData = false;
					page.setStart(0);
				}
				if (null != data) {
					for (CommentItemEntity entity : data) {
						datas.add(entity);
					}
					if (data.length < page.getCount()) {
						isNoMoreData = true;
					} else {
						isNoMoreData = false;
					}
				} else {
					isNoMoreData = true;
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				refreshLayout.refreshComplete();
				isNeedClear = false;
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.btn_videodetail_send: {
				final String content = inputView.getText().toString();
				if(StringUtil.isEmptyString(content)){
					showToast("请输入评论");
					return;
				}
				hideSoftKeyboard();
				inputView.setText("");
				SendCommentRequest request = new SendCommentRequest();
				request.setUser_id(UserUtils.getUserid());
				request.setVideo_id(videoEntity.getId());
				request.setContent(content);
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						RegDataEntity userInfo = UserUtils.getUserInfo();
						CommentItemEntity entity = new CommentItemEntity();
						entity.setContent(content);
						entity.setCreated_at(DateUtil.getUTCTimeString(System.currentTimeMillis()));
						entity.setId(data);
						entity.setVideo_id(videoEntity.getId());
						entity.setUser(userInfo.getUser());
						datas.add(0, entity);
						videoEntity.setComm_count(videoEntity.getComm_count() + 1);
						commentCountView.setText(String.valueOf(videoEntity.getComm_count()));
						commentCount1View.setText(String.valueOf(videoEntity.getComm_count()));
						Intent intent = new Intent();
						intent.putExtra(VIDEOENTITY,GsonUtils.objToJson(videoEntity));
						setResult(RESULT_OK,intent);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFailed(int code, HttpException error, String reason) {
						hideLoading();
						showToast(reason);
					}
				});
				showLoading();
				request.getDataFromServer();
				break;
			}
			case R.id.image_videodetail_avatar:{
				if(videoEntity.getUser().is_acting()){
					GetModelInfoRequest request = new GetModelInfoRequest();
					request.setUserid(videoEntity.getUser().getId());
					request.setNetworkListener(new CommonNetworkCallback<ModelInfoEntity>() {
						@Override
						public void onSuccess(ModelInfoEntity data) {
							hideLoading();
							Intent intent = new Intent(VideoDetailActivity.this, ModelDetailActivity.class);
							intent.putExtra(ModelDetailActivity.USER_ENTITY, GsonUtils.objToJson(videoEntity.getUser()));
							intent.putExtra(ModelDetailActivity.MODEL_ENTITY,GsonUtils.objToJson(data));
							startActivity(intent);
						}

						@Override
						public void onFailed(int code, HttpException error, String reason) {
							hideLoading();
							showToast(reason);
						}
					});
					showLoading();
					request.getDataFromServer();
				}
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int request, int result, Intent arg2) {
		super.onActivityResult(request, result, arg2);
		if (result == RESULT_OK) {
			switch (request) {
			case REQUEST_GIFT: {
				String ss = arg2.getStringExtra(GiftActivity.GIFT_ENTITY);
				GiftItemEntity entity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
				Intent intent = new Intent(this, GiftConfirmActivity.class);
				intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, GsonUtils.objToJson(entity));
				intent.putExtra(GiftConfirmActivity.SHOULDSHOW_TIPS, true);
				intent.putExtra(GiftConfirmActivity.USER_ID, videoEntity.getUser().getId());
				startActivity(intent);
				break;
			}
			case REQUEST_REPORT:{
				String content = arg2.getStringExtra(CommonInputActivity.VALUE);
				ReportVideoRequest requestEntity = new ReportVideoRequest();
				requestEntity.setUserid(videoEntity.getUser().getId());
				requestEntity.setReportid(UserUtils.getUserid());
				requestEntity.setVideoid(videoEntity.getId());
				requestEntity.setContent(content);
				requestEntity.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						showToast("举报成功！");
					}

					@Override
					public void onFailed(int code, HttpException error, String reason) {
						hideLoading();
						showToast(reason);
					}
				});
				showLoading();
				requestEntity.getDataFromServer();
				break;
			}
			}
		}
	}

}
