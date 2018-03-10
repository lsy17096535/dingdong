package com.intexh.bidong.me;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.CashApplyItemEntity;
import com.intexh.bidong.userentity.CashApplyItemEntity.MonthInfo;
import com.intexh.bidong.userentity.CashApplyItemEntityEx;

public class CashApplyAdapter extends BaseAdapter {
	
	public interface OnCashApplyListener{
		public void onClickItem(CashApplyItemEntity entity);
	}
	
	private List<CashApplyItemEntity> datas = null;
	private OnCashApplyListener mListener = null;
	private int mode = CashApplyRecordsActivity.MODE_CASHAPPLY;
	
	public void setmListener(OnCashApplyListener mListener) {
		this.mListener = mListener;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setDatas(List<CashApplyItemEntity> datas) {
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
	public CashApplyItemEntity getItem(int arg0) {
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
	
	private OnClickListener cancelListener =  new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Integer index = (Integer)arg0.getTag();
			if(null != mListener){
				mListener.onClickItem(getItem(index));
			}
		}
	};

	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			convertView = LayoutInflater.from(arg2.getContext()).inflate(R.layout.listitem_cashapply, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		CashApplyItemEntity entity = getItem(index);
		MonthInfo monthInfo = entity.getShowMonth();
		if(0 == index){
			holder.monthView.setVisibility(View.VISIBLE);
			holder.monthView.setText(monthInfo.showMonth);
		}else{
			CashApplyItemEntity preEntity = getItem(index-1);
			MonthInfo preMonthInfo = preEntity.getShowMonth();
			if(monthInfo.realMonth == preMonthInfo.realMonth){
				holder.monthView.setVisibility(View.GONE);
			}else{
				holder.monthView.setVisibility(View.VISIBLE);
				holder.monthView.setText(monthInfo.showMonth);
			}
		}
		holder.weekdayView.setText(monthInfo.week);
		holder.timeView.setText(monthInfo.time);
		holder.valueView.setText(entity.getAmount() + "");
		if(mode == CashApplyRecordsActivity.MODE_CASHAPPLY){
			holder.moneyView.setText("￥" + entity.getAmount());
			holder.statusView.setTag(index);
			switch(((CashApplyItemEntityEx)entity).getStatus()){
			case 0:{
				holder.statusView.setText("取消");
				holder.statusView.setTextColor(Color.parseColor("#ffffff"));
				holder.statusView.setBackgroundResource(R.drawable.selector_login);
				holder.statusView.setOnClickListener(cancelListener);
				break;
			}
			case 1:{
				holder.statusView.setText("已打款");
				holder.statusView.setTextColor(Color.parseColor("#6eb92f"));
				holder.statusView.setBackgroundResource(R.color.transparent);
				holder.statusView.setOnClickListener(null);
				break;
			}
			case 2:{
				holder.statusView.setText("已取消");
				holder.statusView.setTextColor(Color.parseColor("#c6c6c6"));
				holder.statusView.setBackgroundResource(R.color.transparent);
				holder.statusView.setOnClickListener(null);
				break;
			}
			case 3:{
				holder.statusView.setText("处理中");
				holder.statusView.setTextColor(Color.parseColor("#aaaaaa"));
				holder.statusView.setBackgroundResource(R.color.transparent);
				holder.statusView.setOnClickListener(null);
				break;
			}
			}
		}else{
			holder.moneyView.setText("￥" + entity.getAmount());
			holder.statusLayoutView.setVisibility(View.GONE);
		}
		if(index == getCount()-1){
			holder.divider.setVisibility(View.GONE);
			holder.fullDivider.setVisibility(View.VISIBLE);
		}else{
			CashApplyItemEntity preEntity = getItem(index+1);
			MonthInfo preMonthInfo = preEntity.getShowMonth();
			if(preMonthInfo.realMonth == monthInfo.realMonth){
				holder.divider.setVisibility(View.VISIBLE);
				holder.fullDivider.setVisibility(View.GONE);
			}else{
				holder.divider.setVisibility(View.GONE);
				holder.fullDivider.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.txt_cashapply_month)
		TextView monthView;
		@ViewInject(R.id.txt_cashapply_weekday)
		TextView weekdayView;
		@ViewInject(R.id.txt_cashapply_time)
		TextView timeView;
		@ViewInject(R.id.txt_cashapply_value)
		TextView valueView;
		@ViewInject(R.id.btn_cashapply_money)
		Button moneyView;
		@ViewInject(R.id.layout_cashapply_divider)
		private View divider;
		@ViewInject(R.id.layout_cashapply_fulldivider)
		private View fullDivider;
		@ViewInject(R.id.btn_cashapply_status)
		private Button statusView;
		@ViewInject(R.id.layout_cashapply_status)
		private View statusLayoutView;
	}
	
}
