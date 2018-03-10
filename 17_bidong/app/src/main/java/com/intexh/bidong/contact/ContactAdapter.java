package com.intexh.bidong.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

public class ContactAdapter extends BaseAdapter implements SectionIndexer{

	public interface OnContactMenuClickListener{
		public void didClickUnBlock(int index);
		public void didClickBlock(int index);
		public void didClickDelete(int index);
	}
	
	private List<FriendItemEntity> datas = null;
	private OnContactMenuClickListener menuListener = null;
	private OnItemClickListener itemClickListener = null;
	
	public void setItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setMenuListener(OnContactMenuClickListener menuListener) {
		this.menuListener = menuListener;
	}

	public void setDatas(List<FriendItemEntity> datas) {
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
	public FriendItemEntity getItem(int arg0) {
		if(null != datas){
			return datas.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	private OnClickListener blockListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(null != menuListener){
				Integer index = (Integer)arg0.getTag();
				SwipeLayout layout = (SwipeLayout)arg0.getTag(R.id.tag_first);
				layout.close();
				FriendItemEntity entity = getItem(index);
				if(entity.getStatus() == 0){
					menuListener.didClickBlock((Integer)arg0.getTag());
				}else if(entity.getStatus() == 1){
					menuListener.didClickUnBlock((Integer)arg0.getTag());
				}
			}
		}
	};
	
	private OnClickListener deleteListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			SwipeLayout layout = (SwipeLayout)arg0.getTag(R.id.tag_first);
			layout.close();
			if(null != menuListener){
				menuListener.didClickDelete((Integer)arg0.getTag());
			}
		}
	};
	
	private OnClickListener itemListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(null != itemClickListener){
				itemClickListener.onItemClick(null, arg0, (Integer)(arg0.getTag()), 0);
			}
		}
	};
	
	@Override
	public View getView(int index, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(null == convertView){
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(arg2.getContext());
			convertView = inflater.inflate(R.layout.item_contact, null);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
			holder.menublockView.setOnClickListener(blockListener);
			holder.menuDeleteView.setOnClickListener(deleteListener);
			holder.containerView.setOnClickListener(itemListener);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		FriendItemEntity curEntity = getItem(index);
		if(0 == index){
			holder.sectionView.setVisibility(View.VISIBLE);
			holder.sectionNameView.setText(curEntity.getSort_letter().toUpperCase());
		}else{
			FriendItemEntity entity = getItem(index-1);
			String lastSortKey = entity.getSort_letter();
			String curSortKey = curEntity.getSort_letter();
			if(lastSortKey.equals(curSortKey)){
				holder.sectionView.setVisibility(View.GONE);
			}else{
				holder.sectionView.setVisibility(View.VISIBLE);
				holder.sectionNameView.setText(curEntity.getSort_letter().toUpperCase());
			}
		}
		if(curEntity.getStatus() == 0){
			holder.menublockView.setText("屏蔽");
			holder.menublockView.setVisibility(View.VISIBLE);
			holder.menuDeleteView.setVisibility(View.VISIBLE);
		}else if(1 == curEntity.getStatus()){
			holder.menublockView.setText("解除屏蔽");
			holder.menublockView.setVisibility(View.VISIBLE);
			holder.menuDeleteView.setVisibility(View.GONE);
		}else{
			holder.menublockView.setVisibility(View.GONE);
			holder.menuDeleteView.setVisibility(View.GONE);
		}
		switch(curEntity.getOppo_status()){////-1不是对方好友	0是对方好友	1被对方屏蔽	2被对方删除
		case -1:{
			holder.statusView.setText("待接受");
			break;
		}
		case 0:{
			holder.statusView.setText("");
			break;
		}
		case 1:{
			holder.statusView.setText("被屏蔽");
			break;
		}
		case 2:{
			holder.statusView.setText("被删除");
			break;
		}
		}
		ImageUtils.loadAvatarDefaultImage(curEntity.getFans().getAvatar(), holder.avatarView);
		holder.infoView.setText(curEntity.getFans().getAlias() + " ");

		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == curEntity.getFans().getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;
		}
		holder.ageView.setBackgroundColor(PPStarApplication.getInstance().getResources().getColor(genderColorId));
		holder.ageView.setText(PPStarApplication.getInstance().getString(genderFlag) + curEntity.getFans().getAge() + " ");

		holder.signView.setText(curEntity.getFans().getSignature());
		holder.menublockView.setTag(index);
		holder.menuDeleteView.setTag(index);
		holder.containerView.setTag(index);
		holder.menublockView.setTag(R.id.tag_first,holder.swipeLayout);
		holder.menuDeleteView.setTag(R.id.tag_first,holder.swipeLayout);
		holder.containerView.setTag(R.id.tag_first,holder.swipeLayout);
		return convertView;
	}
	
	
	private class ViewHolder{
		@ViewInject(R.id.swipe)
		SwipeLayout swipeLayout;
		@ViewInject(R.id.layout_contactitem_section)
		View sectionView;
		@ViewInject(R.id.txt_contactsection_name)
		TextView sectionNameView;
		@ViewInject(R.id.image_contactitem_avatar)
		ImageView avatarView;
		@ViewInject(R.id.txt_contactinfo_info)
		TextView infoView;
		@ViewInject(R.id.txt_contactinfo_age)
		TextView ageView;
		@ViewInject(R.id.txt_contactinfo_sign)
		TextView signView;
		@ViewInject(R.id.txt_contactitem_menublock)
		TextView menublockView;
		@ViewInject(R.id.txt_contactitem_menudelete)
		TextView menuDeleteView;
		@ViewInject(R.id.layout_contactitem_contentcontainer)
		View containerView;
		@ViewInject(R.id.txt_contactinfo_status)
		TextView statusView;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < datas.size(); i++) {  
			String sortKey = datas.get(i).getSort_letter();//mNicks[i].getSort_key();
            String l = sortKey.substring(0,1); 
            char firstChar = l.toUpperCase().charAt(0);  
            if (firstChar == section) {  
                return i;  
            } 
        }
		return -1;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

}
