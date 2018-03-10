package com.intexh.bidong.main.square;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.main.square.MainSquareHotVideoAdapter.ViewHolder;
import com.intexh.bidong.userentity.HotVideoEntity;
import com.intexh.bidong.utils.ImageUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainSquareHotVideoAdapter extends RecyclerView.Adapter<ViewHolder> {
	
	private List<HotVideoEntity> userList = null;
	private LayoutInflater mInflater = null;
	private boolean isSelf = false;
	
	
	
	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public void setDatas(List<HotVideoEntity> datas){
		userList = datas;
		notifyDataSetChanged();
	}
	
	public MainSquareHotVideoAdapter(Context context){
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder{
		
		@ViewInject(R.id.txt_hotvideo_commentcount)
		TextView commentcountTxt;
		@ViewInject(R.id.txt_hotvideo_playcount)
		TextView playcountTxt;
		@ViewInject(R.id.txt_hotvideo_distance)
		TextView distanceTxt;
		@ViewInject(R.id.image_hotvideo_snapshot)
		ImageView snapshotView;
		@ViewInject(R.id.image_hotvideo_avatar)
		ImageView avatarView;
		@ViewInject(R.id.txt_hotvideo_title)
		TextView titleView;
		@ViewInject(R.id.txt_hotvideo_username)
		TextView usernameView;
		@ViewInject(R.id.txt_hotvideo_info)
		TextView infoView;
		@ViewInject(R.id.image_hotvideo_gender)
		ImageView genderView;
	
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
			if(isSelf){
				avatarView.setVisibility(View.GONE);
				titleView.setText("");
				usernameView.setText("");
				infoView.setText("");
				genderView.setVisibility(View.GONE);
				titleView = usernameView;
			}
		}
	}

	@Override
	public int getItemCount() {
		if(null != userList){
			return userList.size();
		}
		return 10;
	}

	protected HotVideoEntity getItem(int index){
		if(null != userList){
			return userList.get(index);
		}
		return null;
	}
	
	public void onBindViewHolder(ViewHolder vh, int index) {
		HotVideoEntity entity = getItem(index);
		ImageUtils.loadSnapshotImage(entity.getSnapshort(), vh.snapshotView, 0.75f);
		vh.commentcountTxt.setText(String.valueOf(entity.getComm_count()));
		vh.playcountTxt.setText(String.valueOf(entity.getPlay_count()));
		vh.titleView.setText(entity.getRemark());
		if(null == entity.getUser()){
			vh.usernameView.setVisibility(View.GONE);
			vh.avatarView.setVisibility(View.GONE);
			vh.distanceTxt.setText("");
			vh.genderView.setVisibility(View.GONE);
		}else{
			String ss = "";
			if(null != entity.getUser().getAlias()){
				ss += entity.getUser().getAlias();
			}
			vh.distanceTxt.setText(entity.getUser().getCity());
			if(!isSelf){
				vh.usernameView.setText(ss);
//				vh.infoView.setText(" " + DateUtil.getAge(entity.getUser().getBirthday()));
				vh.infoView.setText(" " + entity.getUser().getAge());
				if(1 == entity.getUser().getGender()){
					vh.genderView.setImageResource(R.drawable.icon_female);
				}else{
					vh.genderView.setImageResource(R.drawable.icon_male);
				}
				ImageUtils.loadAvatarDefaultImage(entity.getUser().getAvatar(), vh.avatarView);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int index) {
		View view = mInflater.inflate(R.layout.listitem_hotvideo, null);
		return new ViewHolder(view);
	}

}
