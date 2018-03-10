package com.intexh.bidong.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Calendar;

import com.intexh.bidong.R;
import com.intexh.bidong.utils.DateUtil;

public class DatePickerDialog extends Dialog implements android.view.View.OnClickListener{

	public interface OnDatePickerListener{
		public void onDatePickerConfirm(String date);
	}
	
	private View contentView;
	@ViewInject(R.id.datepicker_datepicker_date)
	private DatePicker picker;
	@ViewInject(R.id.btn_datepicker_ok)
	private Button okBtn;
	@ViewInject(R.id.btn_datepicker_cancel)
	private Button cancelBtn;
	private String curDate;
	private OnDatePickerListener mListener = null;
	private int compare = 0; //0不比较，1要求结果大于等于当前时间，2要求结果小于等于当前时间
	
	public void setListener(OnDatePickerListener mListener) {
		this.mListener = mListener;
	}

	public void setCompare(int compare){
		this.compare = compare;
	}

	public DatePickerDialog(Context context,String ss) {
		super(context,R.style.MyDialogStyle);
		curDate = ss;
	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    getWindow().setAttributes(lp);
		if(null == contentView){
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.dialog_goodsinventory, null);
			contentView = view;
		}
		setContentView(contentView);
		ViewUtils.inject(this, contentView);
		long time = DateUtil.getDayTimestamp(curDate);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		picker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		cancelBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
	}
	
	private Toast toast = null;

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
			case R.id.btn_datepicker_cancel:{
				dismiss();
				break;
			}
			case R.id.btn_datepicker_ok:{
				if(null != mListener){
					Calendar cal = Calendar.getInstance();
					cal.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
					long selectTime = cal.getTimeInMillis();
					if(null != toast){
						toast.cancel();
					}
					if(compare == 1 && selectTime < System.currentTimeMillis()){
						toast = Toast.makeText(getContext(), "选择日期不能小于当前日期", Toast.LENGTH_LONG);
						toast.show();
						return ;
					}
					if(compare == 2 && selectTime > System.currentTimeMillis()){
						toast = Toast.makeText(getContext(), "选择日期不能大于当前日期", Toast.LENGTH_LONG);
						toast.show();
						return ;
					}
					mListener.onDatePickerConfirm(DateUtil.timestampToYMDay(cal.getTimeInMillis()));
					dismiss();
				}
				break;
			}
		}
	}
	
}
