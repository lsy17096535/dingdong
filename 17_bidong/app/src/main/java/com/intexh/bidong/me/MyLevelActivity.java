package com.intexh.bidong.me;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.LevelItemEntity;
import com.intexh.bidong.userentity.LevelResultEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.ProgressLayout;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

public class MyLevelActivity extends BaseTitleActivity {

	@ViewInject(R.id.image_level_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.image_level_level)
	private ImageView levelIconView;
	@ViewInject(R.id.txt_level_name)
	private TextView nameView;
	@ViewInject(R.id.txt_level_level)
	private TextView levelView;
	@ViewInject(R.id.txt_level_progress)
	private TextView progressView;
	@ViewInject(R.id.list_level_main)
	private ListView mainView;
//	@ViewInject(R.id.layout_level_progress)
//	private ProgressLayout progressLayoutView;
	@ViewInject(R.id.txt_level_tips)
	private TextView levelTipsTxt;
	@ViewInject(R.id.layout_level_progresscontainer)
	private ProgressLayout containerView;
	private List<LevelItemEntity> datas = new ArrayList<LevelItemEntity>();
	private LevelAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_level);
		setTitleText(R.string.title_level);
		adapter = new LevelAdapter();
		adapter.setDatas(datas);
		mainView.setAdapter(adapter);
		nameView.setText(UserUtils.getUserInfo().getUser().getAlias());
		ImageUtils.loadAvatarDefaultImage(UserUtils.getUserInfo().getUser().getAvatar(), avatarView);
		if(1 == UserUtils.getUserInfo().getUser().getGender()){
			
		}else{
			levelTipsTxt.setText("充值累计值");
		}
		getLevelInfo();
	}
	
	private void getLevelInfo(){
		MyLevelRequest request = new MyLevelRequest();
		request.setGender(UserUtils.getUserInfo().getUser().getGender());
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<LevelResultEntity>() {

			@Override
			public void onSuccess(LevelResultEntity data) {
				hideLoading();
				if(null != data){
					levelIconView.setImageResource(MeFragment.getLevelResId(data.getLevel().getLevel()));
					levelView.setText("LV" + data.getLevel().getLevel());
					LevelItemEntity[] list = data.getList();
					LevelItemEntity entity = null;
					datas.clear();
					for(LevelItemEntity levelEntity : list){
						datas.add(levelEntity);
						if(levelEntity.getLevel() == data.getLevel().getLevel()){
							entity = levelEntity;//list[data.getLevel().getLevel()-1];
						}
					}
					int amount = data.getLevel().getAmount();
					int start = entity.getLstart();
					int end = entity.getLend() + 1;
					progressView.setText(String.valueOf(amount) + "/" + String.valueOf(end));
//					int offset = end - start;
					double realWeight = containerView.getWidth();
					realWeight = (amount)*100/end;
					containerView.setValue((int)realWeight);
					adapter.notifyDataSetChanged();
					
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.getDataFromServer();
	}
}
