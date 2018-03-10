package com.intexh.bidong.me;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.userentity.LevelItemEntity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LevelAdapter extends BaseAdapter {

	private List<LevelItemEntity> datas = null;

	public void setDatas(List<LevelItemEntity> datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		if(null != datas){
			return datas.size();
		}
		return 0;
	}

	@Override
	public LevelItemEntity getItem(int arg0) {
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
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			LayoutInflater inflater = LayoutInflater.from(arg2.getContext());
			convertView = inflater.inflate(R.layout.listitem_level, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		LevelItemEntity entity = getItem(index);
		holder.levelView.setText("LV" + String.format("%2d", entity.getLevel()));
		holder.valueView.setText(String.valueOf(entity.getLstart()));
		holder.iconView.setImageResource(MeFragment.getLevelResId(entity.getLevel()));
		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.txt_itemlevel_level)
		TextView levelView;
		@ViewInject(R.id.txt_itemlevel_value)
		TextView valueView;
		@ViewInject(R.id.image_itemlevel_icon)
		ImageView iconView;
	}
	
}
