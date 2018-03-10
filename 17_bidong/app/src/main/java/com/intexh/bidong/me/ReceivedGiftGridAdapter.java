package com.intexh.bidong.me;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.me.ReceivedGiftGridAdapter.ViewHolder;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.utils.ImageUtils;

import com.jauker.widget.BadgeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedGiftGridAdapter extends RecyclerView.Adapter<ViewHolder> {

	private List<GiftItemEntity> datas = null;

	public void setDatas(List<GiftItemEntity> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public ReceivedGiftGridAdapter(Context context) {
		super();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@ViewInject(R.id.circle_gift_icon)
		CircleImageView iconView;
		@ViewInject(R.id.txt_gift_name)
		TextView nameView;
		@ViewInject(R.id.image_gift_icon)
		ImageView unitView;
		BadgeView badgeView;

		public ViewHolder(View itemView) {
			super(itemView);
			ViewUtils.inject(this, itemView);
		}

	}

	protected GiftItemEntity getItem(int index) {
		if (null != datas) {
			return datas.get(index);
		}
		return null;
	}

	@Override
	public int getItemCount() {
		if (null != datas) {
			return datas.size();
		}
		return 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int arg1) {
		GiftItemEntity entity = getItem(arg1);
		if (null == viewHolder.badgeView) {
			viewHolder.badgeView = new BadgeView(
					viewHolder.iconView.getContext());
			viewHolder.badgeView.setTargetView(viewHolder.iconView);
			viewHolder.unitView.setVisibility(View.VISIBLE);
		}
		viewHolder.badgeView.setBadgeCount(entity.getCount());
		ImageUtils.loadAvatarDefaultImage(entity.getUrl(), viewHolder.iconView);
		viewHolder.nameView.setText(String.valueOf(entity.getValue()));
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		View view = LayoutInflater.from(arg0.getContext()).inflate(
				R.layout.griditem_gift, null);
		return new ViewHolder(view);
	}

}
