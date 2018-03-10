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

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.main.square.MainSquareOffServiceAdapter.ViewHolder;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;

import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class MainSquareOffServiceAdapter extends RecyclerView.Adapter<ViewHolder> {

	private List<OffServiceEntity> userList = null;
	private LayoutInflater mInflater = null;
	private boolean isSelf = false;

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public void setDatas(List<OffServiceEntity> datas){
		userList = datas;
		notifyDataSetChanged();
	}

	public MainSquareOffServiceAdapter(Context context){
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder{
		@ViewInject(R.id.txt_nearoffservice_descr)
		TextView descrTxt;
		@ViewInject(R.id.txt_nearoffservice_itemnameprice)
		TextView namePriceTxt;
		@ViewInject(R.id.image_nearoffservice_itemicon)
		ImageView itemIconView;
		@ViewInject(R.id.image_nearoffservice_avatar)
		ImageView avatarView;
		@ViewInject(R.id.txt_nearoffservice_username)
		TextView usernameView;
		@ViewInject(R.id.txt_nearoffservice_age)
		TextView ageView;
		@ViewInject(R.id.txt_nearoffservice_occup)
		TextView occupView;
		@ViewInject(R.id.txt_nearoffservice_useraddr)
		TextView userAddrView;
		@ViewInject(R.id.txt_nearoffservice_distance)
		TextView userDistanceView;
	
		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
			if(isSelf){
				avatarView.setVisibility(View.GONE);
				usernameView.setText("");
				ageView.setText("");
				occupView.setText("");
				userAddrView.setText("");
				userDistanceView.setText("");
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

	protected OffServiceEntity getItem(int index){
		if(null != userList){
			return userList.get(index);
		}
		return null;
	}
	
	public void onBindViewHolder(ViewHolder vh, int index) {
		OffServiceEntity entity = getItem(index);
		ImageUtils.loadOffServiceItemImage(entity.getItem().getUri(), vh.itemIconView);
		String price = entity.getPrice();
		if(price.equals("0")){
			price = "免费";
		}
		else{
			price = price + "金币/" + entity.getUnit();
		}
		vh.namePriceTxt.setText(entity.getItem().getName() + " " + price);
		vh.descrTxt.setText(entity.getDescr());
		if(null == entity.getUser()){
			vh.usernameView.setVisibility(View.GONE);
			vh.avatarView.setVisibility(View.GONE);
			vh.occupView.setText("");
			vh.userDistanceView.setText("");
			vh.userAddrView.setText("");
		}else{
			String ss = "";
			if(null != entity.getUser().getAlias()){
				ss += entity.getUser().getAlias();
			}

			if(!isSelf){

				ImageUtils.loadMiddleCornerAvatarImage(entity.getUser().getAvatar(), PtrLocalDisplay.dp2px(6),vh.avatarView);
				vh.usernameView.setText(ss);

				int genderColorId = R.color.text_bg_color_red; //女色
//				int genderFlag = R.string.femal_flag;
				int genderFlag = R.drawable.icon_female1;
				if(2 == entity.getUser().getGender()){  //男
					genderColorId = R.color.text_bg_color_blue;
//					genderFlag = R.string.male_flag;
					genderFlag = R.drawable.icon_male1;
				}
				vh.ageView.setBackgroundColor(PPStarApplication.getInstance().getResources().getColor(genderColorId));
				vh.ageView.setCompoundDrawablesWithIntrinsicBounds(genderFlag,0,0,0 );
				vh.ageView.setText(entity.getUser().getAge() + " ");

				vh.occupView.setText(" " + entity.getUser().getOccup() + " ");
				String addr = entity.getUser().getCity() + entity.getUser().getDistrict();
				vh.userAddrView.setText(addr);
				vh.userDistanceView.setText(StringUtil.getDistanceShortStr(entity.getDistance())+"·");
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int index) {
		View view = mInflater.inflate(R.layout.listitem_nearbyoffservice, null);
		return new ViewHolder(view);
	}

}
