package com.intexh.bidong.userentity;

import java.util.Calendar;

import com.intexh.bidong.utils.DateUtil;

public class CashApplyItemEntity {
	public class MonthInfo {
		public int realMonth; // year*12 + month
		public String showMonth;
		public String week;
		public String time;
	}

	private String id;
	private int amount;
	private int actual_amount;
	private String user_id;
	private String pay_uri;

	public String getPay_uri() {
		return pay_uri;
	}

	// private int status;
	private String remark;
	private String created_at;
	private String updated_at;
	private MonthInfo showMonth;

	public int getActual_amount() {
		return actual_amount;
	}

	public MonthInfo getShowMonth() {
		if (null == showMonth) {
			long time = DateUtil.getTimestamp(created_at);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			Calendar now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
			showMonth = new MonthInfo();
			int realMonth = cal.get(Calendar.YEAR) * 12
					+ cal.get(Calendar.MONTH);
			int nowRealMonth = now.get(Calendar.YEAR) * 12
					+ now.get(Calendar.MONTH);
			if (realMonth == nowRealMonth) {
				showMonth.showMonth = "本月";
			} else {
				showMonth.showMonth = cal.get(Calendar.MONTH) + "月";
			}
			showMonth.realMonth = realMonth;
			showMonth.week = DateUtil.getDayOfWeek(time);
			showMonth.time = DateUtil.timestampToMDay(time);
		}
		return showMonth;
	}

	public String getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public String getUser_id() {
		return user_id;
	}

//	public int getStatus() {
//		return status;
//	}

	public String getRemark() {
		return remark;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

}
