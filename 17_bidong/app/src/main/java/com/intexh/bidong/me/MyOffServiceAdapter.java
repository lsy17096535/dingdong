package com.intexh.bidong.me;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;

public class MyOffServiceAdapter extends BaseAdapter {

	public interface OnItemListener{
		public void onClickItem(OffServiceEntity entity);
	}

	public interface OnMenuListener{
		public void didClickDelete(int index);
	}

	private List<OffServiceEntity> datas = null;
	private OnItemListener mListener = null;
	private OnMenuListener menuListener = null;

	public void setItemListener(OnItemListener mListener) {
		this.mListener = mListener;
	}
	public void setMenuListener(OnMenuListener menuListener) {
		this.menuListener = menuListener;
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
			convertView = LayoutInflater.from(arg2.getContext()).inflate(R.layout.listitem_myoffservice, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final OffServiceEntity entity = getItem(index);
		ImageUtils.loadOffServiceItemImage(entity.getItem().getUri(), holder.itemIcon);
		switch(entity.getStatus()){
			case 0:{
				holder.statusView.setText("待审核");
				break;
			}
			case 1:{
				holder.statusView.setText("通过");
				break;
			}
			case 2:{
				holder.statusView.setText("不通过");
				break;
			}
		}
		holder.nameView.setText(entity.getItem().getName());
		holder.timeView.setText(DateUtil.getTimeDiffDesc(entity.getCreated_at()));
		holder.priceView.setText(entity.getPrice() + "金币/" + entity.getUnit());
		holder.descrView.setText(entity.getDescr());

		holder.container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onClickItem(entity);
			}
		});

		holder.menuDeleteView.setTag(index);
		holder.menuDeleteView.setTag(R.id.tag_first,holder.swipeLayout);
		holder.menuDeleteView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SwipeLayout layout = (SwipeLayout)v.getTag(R.id.tag_first);
				layout.close();
				if(null != menuListener){
					menuListener.didClickDelete((Integer)v.getTag());
				}
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
		@ViewInject(R.id.txt_myoffservice_item_time)
		TextView timeView;
		@ViewInject(R.id.txt_myoffservice_item_descr)
		TextView descrView;
		@ViewInject(R.id.txt_myoffservice_item_status)
		TextView statusView;

		@ViewInject(R.id.swipe_myoffservice_item)
		SwipeLayout swipeLayout;
		@ViewInject(R.id.swipe_myoffservice_item_bottom_wrapper)
		LinearLayout bottomWrapper;
		@ViewInject(R.id.txt_myoffservice_item_menudelete)
		TextView menuDeleteView;
	}
	
}
