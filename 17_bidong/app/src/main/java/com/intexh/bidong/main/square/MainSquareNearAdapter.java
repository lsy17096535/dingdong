package com.intexh.bidong.main.square;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.main.square.MainSquareNearAdapter.ViewHolder;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;

public class MainSquareNearAdapter extends RecyclerView.Adapter<ViewHolder> {
	
	private List<User> userList = null;
	private LayoutInflater mInflater = null;
	
	public void setDatas(List<User> datas){
		userList = datas;
		notifyDataSetChanged();
	}
	
	public MainSquareNearAdapter(Context context){
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder{

		@ViewInject(R.id.image_mainsquare_nearitem_avatar)
		ImageView avatarView;
		@ViewInject(R.id.image_mainsquare_nearitem_paly)
		ImageView playIcon;
		@ViewInject(R.id.txt_mainsquare_nearitem_distance)
		TextView distanceView;
		@ViewInject(R.id.txt_mainsquare_nearitem_name)
		TextView nameView;
		@ViewInject(R.id.txt_mainsquare_nearitem_sign)
		TextView signView;
		@ViewInject(R.id.image_mainsquare_nearitem_gender)
		ImageView genderView;
		@ViewInject(R.id.txt_mainsquare_nearitem_age)
		TextView ageView;

		int index;
		
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
		}
	}

	@Override
	public int getItemCount() {
		if(null != userList){
			return userList.size();
		}
		return 0;
	}

	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int index) {
		View view = mInflater.inflate(R.layout.griditem_mainsquarenear, null);
		return new ViewHolder(view);
	}
	
	protected User getItem(int index) {
		if(null != userList){
			return userList.get(index);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int arg1) {
		holder.index = arg1;
		User user = getItem(arg1);
		ImageUtils.loadMiddleAvatarImage(user.getAvatar(), holder.avatarView, 1.0f);
		if(null != user.getVideo_id()){
			holder.playIcon.setVisibility(View.VISIBLE);
		}
		else{
			holder.playIcon.setVisibility(View.GONE);
		}
		holder.nameView.setText(user.getAlias());
		if(1 == user.getGender()){
			holder.genderView.setImageResource(R.drawable.icon_female);
		}else{
			holder.genderView.setImageResource(R.drawable.icon_male);
		}
		holder.ageView.setText(String.valueOf(user.getAge()));
		holder.signView.setText(user.getSignature());

		double[] upoint = StringUtil.getLocPoint(user.getLoc());
		if(null != upoint){
			holder.distanceView.setText(StringUtil.getDistanceStr(upoint));
		}
	}

}
