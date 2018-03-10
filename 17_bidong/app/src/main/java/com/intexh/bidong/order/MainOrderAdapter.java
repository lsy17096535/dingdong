package com.intexh.bidong.order;

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

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.order.MainOrderAdapter.ViewHolder;
import com.intexh.bidong.userentity.OrderEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;

public class MainOrderAdapter extends Adapter<ViewHolder> {

	public static final int ORDER_SEND = 0;
	public static final int ORDER_RECEIVE = 1;

	private int mode = ORDER_SEND;

	private List<OrderEntity> datas = null;

	public MainOrderAdapter(int mode){
		super();
		this.mode = mode;
	}
	
	public void setDatas(List<OrderEntity> datas) {
		this.datas = datas;
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		@ViewInject(R.id.image_order_avatar)
		ImageView avatarView;
		@ViewInject(R.id.image_order_gift_icon)
		ImageView giftIconView;
		@ViewInject(R.id.txt_order_giftprice)
		TextView giftPriceView;
		@ViewInject(R.id.txt_order_giftname)
		TextView giftNameView;
		@ViewInject(R.id.image_order_item_icon)
		ImageView itemIconView;
		@ViewInject(R.id.txt_order_itemprice)
		TextView itemPriceView;
		@ViewInject(R.id.txt_order_itemname)
		TextView itemNameView;
		@ViewInject(R.id.txt_order_username)
		TextView usernameView;
		@ViewInject(R.id.txt_order_age)
		TextView ageView;
		@ViewInject(R.id.txt_order_useraddr)
		TextView addrView;
		@ViewInject(R.id.txt_order_userdis)
		TextView distanceView;
		@ViewInject(R.id.txt_order_occup)
		TextView occupView;
		@ViewInject(R.id.txt_order_ctime)
		TextView ctimeView;
		@ViewInject(R.id.txt_order_otime)
		TextView otimeView;
		@ViewInject(R.id.txt_order_status)
		TextView statusView;
		
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
		}
	}

	protected OrderEntity getItem(int index){
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

	private void setViewValue(ViewHolder holder, User user){
		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == user.getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;
		}
		ImageUtils.loadAvatarDefaultImage(user.getAvatar(), holder.avatarView);
		holder.usernameView.setText(user.getAlias());
		holder.ageView.setBackgroundColor(PPStarApplication.getInstance().getResources().getColor(genderColorId));
		holder.ageView.setText(PPStarApplication.getInstance().getString(genderFlag) + user.getAge() + " ");

		holder.occupView.setText(" " + user.getOccup() + " ");
		holder.addrView.setText(user.getCity() + user.getDistrict());

		double[] upoint = StringUtil.getLocPoint(user.getLoc());
		if(null != upoint){
			holder.distanceView.setText(StringUtil.getDistanceStr(upoint));
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int arg1) {
		OrderEntity entity = getItem(arg1);
		ImageUtils.loadAvatarDefaultImage(entity.getGift().getUrl(), holder.avatarView);
		if(mode == ORDER_RECEIVE){
			setViewValue(holder, entity.getFrom());
		}else{
			setViewValue(holder, entity.getTo());
		}

		holder.ctimeView.setText(DateUtil.getTimeDiffDesc(entity.getCreated_at()));

		holder.itemNameView.setText(entity.getItem().getName());
		String price = entity.getItem().getPrice();
		if("0".equals(price)){
			price = "免费";
		}
		else {
			price = price  + "金币/" + entity.getItem().getUnit();
		}
		holder.itemPriceView.setText(price);
		ImageUtils.loadOffServiceItemImage(entity.getItem().getUri(), holder.itemIconView);

		holder.giftNameView.setText(entity.getGift().getName());
		holder.giftPriceView.setText(entity.getGift().getPrice() + " 金币");
		String url = entity.getGift().getUri();
		if(null != url && url.contains(".png")){
			url = url.replace(".png", "_b.png");
		}
		ImageUtils.loadGiftImage(url, holder.giftIconView);

		holder.otimeView.setText(DateUtil.getMonthDay(entity.getApp_time()));
		switch(entity.getStatus()){
			case 0:{
				holder.statusView.setText("待接单");
				break;
			}
			case 1:{
				if(entity.getPay_status() == 2){
					holder.statusView.setText("已付款");
				}
				else if(entity.getPay_status() == 3){
					holder.statusView.setText("已线下付款");
				}
				else{
					holder.statusView.setText("已接单");
				}
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
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
		return new ViewHolder(inflater.inflate(R.layout.listitem_order, null));
	}
	
}
