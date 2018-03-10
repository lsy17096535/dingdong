package com.intexh.bidong.gift;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.gift.MainGiftSendAdapter.ViewHolder;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;

public class MainGiftSendAdapter extends Adapter<ViewHolder> {

	public static final int GIFT_SEND = 0;
	public static final int GIFT_RECEIVE = 1;
	
	private int mode = GIFT_SEND;
	
	private List<SendGiftItemEntity> datas = null;
	
	public MainGiftSendAdapter(int mode){
		super();
		this.mode = mode;
	}
	
	public void setDatas(List<SendGiftItemEntity> datas) {
		this.datas = datas;
	}

	class ViewHolder extends RecyclerView.ViewHolder{

		@ViewInject(R.id.image_giftitem_icon)
		ImageView icoNView;
		@ViewInject(R.id.image_giftitem_avatar)
		ImageView avatarView;
		@ViewInject(R.id.txt_giftitem_name)
		TextView nameView;
		@ViewInject(R.id.txt_giftitem_time)
		TextView timeView;
		@ViewInject(R.id.txt_giftitem_status)
		TextView statusView;
		
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
		}
	}

	protected SendGiftItemEntity getItem(int index){
		if(null != datas){
			return datas.get(index);
		}
		return null;
	}
	
	@Override
	public int getItemCount() {
		if(null != datas){
			return datas.size();
		}
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int arg1) {
		SendGiftItemEntity entity = getItem(arg1);
		ImageUtils.loadAvatarDefaultImage(entity.getGift().getUrl(), holder.icoNView);
		if(mode == GIFT_RECEIVE){
			ImageUtils.loadAvatarDefaultImage(entity.getFrom().getAvatar(), holder.avatarView);
		}else{
			ImageUtils.loadAvatarDefaultImage(entity.getTo().getAvatar(), holder.avatarView);
		}
		switch(entity.getStatus()){
		case 0:{
			holder.statusView.setText("待接受");
			break;
		}
		case 1:{
			holder.statusView.setText("已接受");
			break;
		}
		case 2:{
			holder.statusView.setText("已拒绝");
			break;
		}
		case 3:{
			holder.statusView.setText("已取消");
			break;
		}
		case 4:{
			holder.statusView.setText("已删除");
			break;
		}
		}
		holder.nameView.setText(entity.getGift().getName() + " " + entity.getValue() + "金币");
		holder.timeView.setText(DateUtil.timestampToMDHMDate(DateUtil.getTimestamp(entity.getCreated_at())));
	}

	@Override
	public ViewHolder onCreateViewHolder(
			ViewGroup arg0, int arg1) {
		LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
		return new ViewHolder(inflater.inflate(R.layout.listitem_giftsend, null));
	}
	
}
