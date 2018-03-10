package com.intexh.bidong.me;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.util.ArrayList;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

public class MyOffServiceAddActivity extends BaseTitleActivity {
	public static final String MODE_ADD = "add";
	public static final String MODE_EDIT = "edit";

	@ViewInject(R.id.image_title_right)
	private ImageView titleRightView;
	@ViewInject(R.id.image_offservice_add_icon)
	private ImageView iconView;
	@ViewInject(R.id.txt_offservice_add_name)
	private TextView nameTxt;
	@ViewInject(R.id.edit_offservice_add_price)
	private EditText priceTxt;
	@ViewInject(R.id.edit_offservice_add_descr)
	private EditText descrTxt;
	@ViewInject(R.id.radiogroup_offservice_add_unit)
	private RadioGroup unitGroup;
	@ViewInject(R.id.radio_offservice_add_hour)
	private RadioButton hourRadio;
	@ViewInject(R.id.radio_offservice_add_time)
	private RadioButton timeRadio;
	@ViewInject(R.id.radio_offservice_add_day)
	private RadioButton dayRadio;

	@ViewInject(R.id.check_offservice_add_week1)
	private CheckBox week1Check;
	@ViewInject(R.id.check_offservice_add_week2)
	private CheckBox week2Check;
	@ViewInject(R.id.check_offservice_add_week3)
	private CheckBox week3Check;
	@ViewInject(R.id.check_offservice_add_week4)
	private CheckBox week4Check;
	@ViewInject(R.id.check_offservice_add_week5)
	private CheckBox week5Check;
	@ViewInject(R.id.check_offservice_add_week6)
	private CheckBox week6Check;
	@ViewInject(R.id.check_offservice_add_week7)
	private CheckBox week7Check;
	@ViewInject(R.id.btn_offservice_add_save)
	private Button saveButton;

	private static final int REQUEST_NICKNAME = 100;

	private String mode;
	private OffServiceEntity entity;
	private String itemId;
	private String itemName;
	private String itemUri;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_myoffserviceadd);

		mode = getIntent().getStringExtra("mode");
		if(MODE_ADD.equals(mode)){
			setTitleText("发布线下约会");
			saveButton.setText("发  布");
			itemId = getIntent().getStringExtra("item_id");
			itemName = getIntent().getStringExtra("item_name");
			itemUri = getIntent().getStringExtra("item_uri");

			nameTxt.setText(itemName);
			ImageUtils.loadOffServiceItemImage(itemUri, iconView);
		}
		else {
			setTitleText("修改线下约会");
			saveButton.setText("保  存");
			titleRightView.setImageResource(R.drawable.icon_ban_more);
			titleRightView.setVisibility(View.VISIBLE);
			titleRightView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showMore();
				}
			});
			entity = (OffServiceEntity)getIntent().getSerializableExtra("data");
			initData();
		}

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commit();
			}
		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void initData(){
		ImageUtils.loadOffServiceItemImage(entity.getItem().getUri(), iconView);
		nameTxt.setText(entity.getItem().getName());
		priceTxt.setText(entity.getPrice());
		descrTxt.setText(entity.getDescr());
		switch(entity.getUnit()){
			case "小时":
				unitGroup.check(R.id.radio_offservice_add_hour);
				break;
			case "次":
				unitGroup.check(R.id.radio_offservice_add_time);
				break;
			case "天":
				unitGroup.check(R.id.radio_offservice_add_day);
				break;
		}
		for(String time : entity.getIdel_times()){
			switch(time){
				case "周一":
					week1Check.setChecked(true);
					break;
				case "周二":
					week2Check.setChecked(true);
					break;
				case "周三":
					week3Check.setChecked(true);
					break;
				case "周四":
					week4Check.setChecked(true);
					break;
				case "周五":
					week5Check.setChecked(true);
					break;
				case "周六":
					week6Check.setChecked(true);
					break;
				case "周日":
					week7Check.setChecked(true);
					break;
			}
		}
	}
	

	private void commit() {
		UpdateMyOffServiceRequest request = new UpdateMyOffServiceRequest();
		if(MODE_EDIT.equals(mode)){
			request.setId(entity.getId());
		}
		else{
			request.setUser_id(UserUtils.getUserid());
			request.setItem_id(itemId);
		}

		String str = priceTxt.getText().toString();
		if(!StringUtil.isEmptyString(str)){
			if(!RegexUtil.checkDigit(str)){
				showToast(R.string.error_price);
				return ;
			}else{
				int price = Integer.parseInt(str);
				if(price >= 0 && price <= 20000){
					request.setPrice(price);
				}else{
					showToast(R.string.error_price);
					return ;
				}
			}
		}
		else{
			showToast(R.string.error_price);
			return;
		}

		switch(unitGroup.getCheckedRadioButtonId()){
			case R.id.radio_offservice_add_hour:
				str = "小时";
				break;
			case R.id.radio_offservice_add_time:
				str = "次";
				break;
			case R.id.radio_offservice_add_day:
				str = "天";
				break;
		}
		request.setUnit(str);

		ArrayList<String> weeks = new ArrayList();
		if(week1Check.isChecked()){
			weeks.add("周一");
		}
		if(week2Check.isChecked()){
			weeks.add("周二");
		}
		if(week3Check.isChecked()){
			weeks.add("周三");
		}
		if(week4Check.isChecked()){
			weeks.add("周四");
		}
		if(week5Check.isChecked()){
			weeks.add("周五");
		}
		if(week6Check.isChecked()){
			weeks.add("周六");
		}
		if(week7Check.isChecked()){
			weeks.add("周日");
		}

		if(weeks.isEmpty()){
			showToast(R.string.error_idel_time);
			return;
		}
		request.setIdel_times(weeks.toArray(new String[weeks.size()]));

		str = descrTxt.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast(R.string.error_descr);
			return;
		}
		request.setDescr(str);

		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				if(MODE_ADD.equals(mode)){
					showToast("服务发布成功");
				} else {
					showToast("服务保存成功");
				}
				hideLoading();
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.getDataFromServer();
	}

	private void deleteOffService(){
		DeleteMyOffServiceRequest request = new DeleteMyOffServiceRequest();
		request.setId(entity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("线下服务删除成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	private void showMore() {
		ActionSheet
			.createBuilder(this, getSupportFragmentManager())
			.setCancelButtonTitle(R.string.common_cancel)
			.setOtherButtonTitles("删除服务", "保存服务")
			.setListener(new ActionSheet.ActionSheetListener() {
				@Override
				public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
				}

				@Override
				public void onOtherButtonClick(ActionSheet actionSheet, int index) {
					switch (index) {
						case 0: {
							deleteOffService();
							break;
						}
						case 1: {
							commit();
							break;
						}
					}
				}

			}).show();
	}
}
