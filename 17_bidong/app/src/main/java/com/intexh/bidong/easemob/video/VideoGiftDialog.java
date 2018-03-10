package com.intexh.bidong.easemob.video;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;

public class VideoGiftDialog extends Dialog {

	public enum VideoGiftDialogMode {
		VideoGiftDialogModeReceive,
		VideoGiftDialogModeSend,
		VideoGiftDialogModeBeg,
	};
	
	public interface ViewGiftDialogListener{
		void onClickSend(SendGiftItemEntity entity);
		void onClickBeg(SendGiftItemEntity entity);
		void onClickCancel();
	}
	
	private VideoGiftDialogMode mode;
	@ViewInject(R.id.txt_videodlg_receive)
	private TextView receiveTxt;
	@ViewInject(R.id.image_videodlg_icon)
	private ImageView iconImageView;
	@ViewInject(R.id.txt_videodlg_name)
	private TextView nameTxt;
	@ViewInject(R.id.btn_videodlg_confirm)
	private Button confirmBtn;
	@ViewInject(R.id.btn_videodlg_beggift)
	private Button beggiftBtn;
	@ViewInject(R.id.btn_videodlg_cancel)
	private Button cancelBtn;
	@ViewInject(R.id.layout_videodlg_receive)
	private View containerView;
	private View contentView;
	private ViewGiftDialogListener mListener = null;
	private SendGiftItemEntity giftEntity = null;
	
	public void setGiftDialogListener(ViewGiftDialogListener listener){
		mListener = listener;
	}
	
	public void setMode(VideoGiftDialogMode mode){
		this.mode = mode;
	}
	
	public void setGiftEntity(SendGiftItemEntity giftEntity) {
		this.giftEntity = giftEntity;
	}

	public VideoGiftDialog(Context context) {
		super(context,R.style.MyDialogStyle);
	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    getWindow().setAttributes(lp);
		if(null == contentView){
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.dialog_video, null);
			contentView = view;
		}
		setContentView(contentView);
		ViewUtils.inject(this, contentView);
		ImageUtils.loadAvatarDefaultImage(giftEntity.getGift().getUrl(), iconImageView);
		if(null != giftEntity.getGift().getName()){
			nameTxt.setText(giftEntity.getGift().getName() + " " + String.valueOf(giftEntity.getGift().getPrice()));
		}else{
			nameTxt.setText(String.valueOf(giftEntity.getGift().getPrice()));
		}
		switch(mode){
		case VideoGiftDialogModeSend:{//送出礼物
			beggiftBtn.setText("送出");
			cancelBtn.setText(R.string.common_refuse);
			if(UserUtils.getUserid().equals(giftEntity.getTo().getId())){
				receiveTxt.setText(giftEntity.getFrom().getAlias() + "要礼物");
			}else{
				receiveTxt.setText(giftEntity.getTo().getAlias() + "要礼物");
			}
			beggiftBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(null != mListener){
						mListener.onClickSend(giftEntity);
					}
					dismiss();
				}
			});
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(null != mListener){
						mListener.onClickCancel();
					}
					dismiss();
				}
			});
			break;
		}
		case VideoGiftDialogModeBeg:{
			receiveTxt.setVisibility(View.GONE);
			beggiftBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(null != mListener){
						mListener.onClickBeg(giftEntity);
					}
					dismiss();
				}
			});
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(null != mListener){
						mListener.onClickCancel();
					}
					dismiss();
				}
			});
			break;
		}
		case VideoGiftDialogModeReceive:{
			if(UserUtils.getUserid().equals(giftEntity.getTo().getId())){
				receiveTxt.setText("收到" + giftEntity.getFrom().getAlias() + "的礼物");
			}else{
				receiveTxt.setText("收到" + giftEntity.getTo().getAlias() + "的礼物");
			}
			containerView.setVisibility(View.GONE);
			confirmBtn.setVisibility(View.GONE);
			iconImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(null != mListener){
						mListener.onClickBeg(giftEntity);
					}
					dismiss();
				}
			});
			break;
		}
		}
	}

	
	
}
