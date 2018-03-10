package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.main.square.MainSquareOffServiceFragment;
import com.intexh.bidong.userentity.OffServiceItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;

public class OffServiceItemsActivity extends BaseTitleActivity implements View.OnClickListener{
	@ViewInject(R.id.layout_offservice_items)
	private LinearLayout containerView;

	private String[] itemids = null;
	private int screenWidth = 0;
	private int screenHeight = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_offserviceitemlist);
		setTitleText("选择服务项目");

		itemids = getIntent().getStringArrayExtra("itemids");

		DisplayMetrics dm = new DisplayMetrics();
		//取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//窗口的宽度
		screenWidth = dm.widthPixels;
		//窗口高度
		screenHeight = dm.heightPixels;

		getItems();
	}
	
	private void getItems(){
		List<OffServiceItemEntity> itemList = MainSquareOffServiceFragment.osItems;
		if(itemList != null && !itemList.isEmpty()){
			buildScreen(itemList.toArray(new OffServiceItemEntity[itemList.size()]));
			return;
		}
		//如果项目数据不存在，则向后台请求
		GetOffServiceItemRequest request = new GetOffServiceItemRequest();
		request.setNetworkListener(new CommonNetworkCallback<OffServiceItemEntity[]>() {
			@Override
			public void onSuccess(OffServiceItemEntity[] data) {
				hideLoading();
				if(null != data){
					buildScreen(data);
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

	private void buildScreen(OffServiceItemEntity[] data){
		final Map<String, List<OffServiceItemEntity>> map = new HashMap();
		final List<String> keys = new ArrayList();
		for(OffServiceItemEntity item : data){
			if(map.containsKey(item.getType_id())){
				map.get(item.getType_id()).add(item);
			}
			else{
				List<OffServiceItemEntity> list = new ArrayList();
				list.add(item);
				keys.add(item.getType_id());
				map.put(item.getType_id(), list);
			}
		}
		Arrays.sort(keys.toArray());
		for(String key : keys){
			List<OffServiceItemEntity> list = map.get(key);
			genLayout(list);
		}
	}

	private void genLayout(List<OffServiceItemEntity> items){
		LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.listitem_offserviceitemblock, null);
		GridLayout gridLayout = (GridLayout)layout.findViewById(R.id.gridlayout_offservice_items);
		TextView typeView = (TextView)layout.findViewById(R.id.label_offservice_item_typename);
		typeView.setText(items.get(0).getType_name());

		for(OffServiceItemEntity item : items){
			LinearLayout itemLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.listitem_offserviceitem, null);
			TextView nameView = (TextView)itemLayout.findViewById(R.id.txt_offservice_item_name);
			ImageView iconView = (ImageView)itemLayout.findViewById(R.id.image_offservice_item_icon);
			nameView.setText(item.getName());
			ImageUtils.loadOffServiceItemImage(item.getUri(), iconView);
			gridLayout.addView(itemLayout, screenWidth/4, LinearLayout.LayoutParams.WRAP_CONTENT);
			itemLayout.setTag(R.id.tag_first, item);
			itemLayout.setOnClickListener(this);
		}

		containerView.addView(layout);
	}

	@Override
	public void onClick(View v) {
		OffServiceItemEntity item = (OffServiceItemEntity)v.getTag(R.id.tag_first);
		if(StringUtil.contain(item.getId(), itemids)){
			showToast("该项目的线下服务已经发布过了！");
			return;
		}
		Intent intent = new Intent(OffServiceItemsActivity.this, MyOffServiceAddActivity.class);
		intent.putExtra("mode", MyOffServiceAddActivity.MODE_ADD);
		intent.putExtra("item_id", item.getId());
		intent.putExtra("item_name", item.getName());
		intent.putExtra("item_uri", item.getUri());
		startActivity(intent);
	}
}
