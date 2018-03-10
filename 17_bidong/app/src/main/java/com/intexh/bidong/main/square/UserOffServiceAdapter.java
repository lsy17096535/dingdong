package com.intexh.bidong.main.square;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.ImageUtils;

public class UserOffServiceAdapter extends BaseAdapter {

	public interface OnItemListener{
		public void onClickItem(OffServiceEntity entity);
	}

	private List<OffServiceEntity> datas = null;
	private OnItemListener mListener = null;

	public void setItemListener(OnItemListener mListener) {
		this.mListener = mListener;
	}

	public void setDatas(List<OffServiceEntity> datas) {
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
	public OffServiceEntity getItem(int arg0) {
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
	public View getView(final int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			convertView = LayoutInflater.from(arg2.getContext()).inflate(R.layout.listitem_useroffservice, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final OffServiceEntity entity = getItem(index);
		ImageUtils.loadOffServiceItemImage(entity.getItem().getUri(), holder.itemIcon);
		holder.nameView.setText(entity.getItem().getName());
		String price = entity.getPrice();
		if(price.equals("0")){
			price = "免费";
		}
		else{
			price = price + "金币/" + entity.getUnit();
		}
		holder.priceView.setText(price);
		holder.descrView.setText(entity.getDescr());

		holder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onClickItem(entity);
			}
		});

		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.layout_myoffservice_item_container)
		RelativeLayout container;
		@ViewInject(R.id.image_myoffservice_item_icon)
		ImageView itemIcon;
		@ViewInject(R.id.txt_myoffservice_item_name)
		TextView nameView;
		@ViewInject(R.id.txt_myoffservice_item_price)
		TextView priceView;
		@ViewInject(R.id.txt_myoffservice_item_descr)
		TextView descrView;
	}
	
}
