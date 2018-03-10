package com.intexh.bidong.me;

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
import com.intexh.bidong.userentity.WatchItemEntity;
import com.intexh.bidong.utils.ImageUtils;

/**
 * Created by shenxin on 15/12/30.
 */
public class VisitMeAdapter extends RecyclerView.Adapter<VisitMeAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.image_watchme_avatar)
        ImageView avatarView;
        @ViewInject(R.id.txt_watchme_username)
        TextView userNameView;
        @ViewInject(R.id.txt_watchme_time)
        TextView timeView;
        View allView;

        public ViewHolder(View view){
            super(view);
            allView = view;
            ViewUtils.inject(this,view);
        }
    }

    private List<WatchItemEntity> datas = null;

    public void setDatas(List<WatchItemEntity> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_watchme,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WatchItemEntity entity = datas.get(position);
        ImageUtils.loadAvatarDefaultImage(entity.getAvatar(), holder.avatarView);
        holder.userNameView.setText(entity.getAlias());
        String ss = "";
        if(entity.getDays() > 30){
            ss = "1个月前";
        }else if(entity.getDays() > 0){
            ss = "" + entity.getDays() + "天前";
        }else if(entity.getHours() > 0){
            ss = "" + entity.getHours() + "小时前";
        }else{
            ss = "刚刚";
        }
        holder.timeView.setText(ss);
        if(position < 4){
            holder.allView.setPadding(0, holder.allView.getContext().getResources().getDimensionPixelSize(R.dimen.spacing),0,0);
        }else{
            holder.allView.setPadding(0, 0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        if(null != datas){
            return datas.size();
        }
        return 0;
    }
}
