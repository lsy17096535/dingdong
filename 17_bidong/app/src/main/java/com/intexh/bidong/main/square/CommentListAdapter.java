package com.intexh.bidong.main.square;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.userentity.CommentItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CommentListAdapter extends BaseAdapter {

	public interface OnCommentListener{
		public void onClickAvatar(CommentItemEntity comment);
	}
	
	private List<CommentItemEntity> datas = null;
	private LayoutInflater mInflater = null;
	private OnCommentListener mCommentListener = null;
	
	public void setCommentListener(OnCommentListener mCommentListener) {
		this.mCommentListener = mCommentListener;
	}

	public void setDatas(List<CommentItemEntity> datas){
		this.datas = datas;
		notifyDataSetChanged();
	}
	
	public CommentListAdapter(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if(null != datas){
			return datas.size();
		}
		return 0;
	}

	@Override
	public CommentItemEntity getItem(int arg0) {
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

	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			int index = (Integer)arg0.getTag();
			CommentItemEntity entity = getItem(index);
			if(null != mCommentListener){
				mCommentListener.onClickAvatar(entity);
			}
		}
	};
	
	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			convertView = mInflater.inflate(R.layout.listitem_comment, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
			ViewUtils.inject(holder, convertView);
			holder.avatarImageView.setOnClickListener(mListener);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		CommentItemEntity entity = getItem(index);
		holder.usernameTxt.setText(entity.getUser().getAlias());
		holder.timeTxt.setText(DateUtil.timestampToMDHMDate(DateUtil.getTimestamp(entity.getCreated_at())));
		holder.contentTxt.setText(entity.getContent());
		ImageUtils.loadAvatarDefaultImage(entity.getUser().getAvatar(), holder.avatarImageView);
		holder.avatarImageView.setTag(index);
		return convertView;
	}

	private class ViewHolder{
		@ViewInject(R.id.circle_comment_avatar)
		ImageView avatarImageView;
		@ViewInject(R.id.txt_comment_username)
		TextView usernameTxt;
		@ViewInject(R.id.txt_comment_time)
		TextView timeTxt;
		@ViewInject(R.id.txt_comment_content)
		TextView contentTxt;
	}
	
}
