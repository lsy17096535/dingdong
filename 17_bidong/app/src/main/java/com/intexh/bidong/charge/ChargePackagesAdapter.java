package com.intexh.bidong.charge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.ChargePackageItemEntity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class ChargePackagesAdapter extends BaseAdapter {

	private List<ChargePackageItemEntity> datas = null;
	private OnItemClickListener itemClickListener = null;
	
	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setDatas(List<ChargePackageItemEntity> datas) {
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
	public ChargePackageItemEntity getItem(int arg0) {
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

	private OnClickListener chargeListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Integer index = (Integer)arg0.getTag();
			if(null != itemClickListener){
				itemClickListener.onItemClick(null, arg0, index, 0);
			}
		}
	};
	
	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(arg2.getContext()).inflate(R.layout.listitem_charge, null);
			ViewUtils.inject(holder, convertView);
			holder.chargeView.setOnClickListener(chargeListener);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		ChargePackageItemEntity entity = getItem(index);
		holder.chargeView.setTag(index);
		holder.chargeView.setText("￥" + String.format("%d", entity.getAmount()));
		holder.priceView.setText(String.valueOf(entity.getValue()));
		if(null != entity.getBadge_name()){
			holder.badgeView.setVisibility(View.VISIBLE);
			holder.badgeNameTxt.setText(entity.getBadge_name());
		}else{
			holder.badgeView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	
	private class ViewHolder{
		@ViewInject(R.id.btn_chargeitem_charge)
		Button chargeView;
		@ViewInject(R.id.txt_chargeitem_price)
		TextView priceView;
		@ViewInject(R.id.layout_charge_badge)
		View badgeView;
		@ViewInject(R.id.txt_badge_name)
		TextView badgeNameTxt;

	}
}
