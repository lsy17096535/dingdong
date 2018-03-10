package com.intexh.bidong.main.square;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.userentity.VideoItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class VideoListAdapter extends BaseAdapter {

	private List<VideoItemEntity> datas = null;
	private LayoutInflater mInflater = null;
	
	public VideoListAdapter(Context context){
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	public void setDatas(List<VideoItemEntity> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(null != datas){
			return datas.size();
		}
		return 4;
	}

	@Override
	public VideoItemEntity getItem(int arg0) {
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
			convertView = mInflater.inflate(R.layout.listitem_video, null);
			holder = new ViewHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		VideoItemEntity entity = getItem(index);
		ImageUtils.loadSnapshotImage(entity.getSnapshort(), holder.snapshotView, 0.8f);
		holder.usernameTxt.setText(entity.getRemark());
		holder.commentCountTxt.setText(String.valueOf(entity.getComm_count()));
		holder.playCountTxt.setText(String.valueOf(entity.getPlay_count()));
		holder.timeView.setText(DateUtil.timestampToMDHMDate(DateUtil.getTimestamp(entity.getCreated_at())));
		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.image_videoitem_snapshot)
		ImageView snapshotView;
		@ViewInject(R.id.txt_videoitem_username)
		TextView usernameTxt;
		@ViewInject(R.id.txt_videoitem_commentcount)
		TextView commentCountTxt;
		@ViewInject(R.id.txt_videoitem_playcount)
		TextView playCountTxt;
		@ViewInject(R.id.txt_videoitem_time)
		TextView timeView;
	}
	
}
