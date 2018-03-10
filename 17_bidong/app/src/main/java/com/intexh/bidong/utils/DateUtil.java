package com.intexh.bidong.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	
//	private static long oneDayTimeValue = 86400000; // 一天的毫秒差值

	
//	public static int getTodayDayLong(String endTime){
//		int ret = 0;
//		String now = timestampToYMDay(System.currentTimeMillis());
//		long start = getDayTime(now);
//		long end = getTimestamp(endTime) + oneDayTimeValue;
//		if(end >= start){
//			ret = (int)((end-start)/oneDayTimeValue);
//		}
//		return ret;
//	}
	
//	public static int getTotalDayLong(String startTime,String endTime){
//		int ret = 0;
//		long start = getTimestamp(startTime);
//		long end = getTimestamp(endTime);
//		if(end >= start){
//			ret = (int)((end-start)/oneDayTimeValue);
//		}
//		return ret;
//	}
	
	public static int getAge(String birthday){
		if(null == birthday){
			return 0;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = dateFormat.parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(null == d){
			long timestap = getTimestamp(birthday);
			d = new Date(timestap);
		}
		int time = d.getYear();
		Date dd = new Date(System.currentTimeMillis());
		return dd.getYear() - time;
	}
	
	// 将字符串转为时间戳
	public static long getDayTime(String date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = dateFormat.parse(date);
			return d.getTime();
		} catch (ParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Date getTimeDate(long time){
		String ss = DateUtil.timestampToDate(time);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date d = null;
		try {
			d = dateFormat.parse(ss);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	//获取 x月x日 格式的字符串
	public static int[] getMonthDayArray(String dateStr) {
		int[] desc = new int[2];
		Date d = getDate(dateStr);
		if(null != d){
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			desc[0] = cal.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
			desc[1] = cal.get(Calendar.DAY_OF_MONTH);    //获取当前天数
		}
		return desc;
	}

	//获取 x月x日 格式的字符串
	public static String getMonthDay(String dateStr) {
		String desc = "未定";
		Date d = getDate(dateStr);
		if(null != d){
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			int month = cal.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
			int day = cal.get(Calendar.DAY_OF_MONTH);    //获取当前天数
			desc = month + "月" + day + "日";
		}
		return desc;
	}

	//计算传入时间和当前时间的差值描述
	public static String getTimeDiffDesc(String dateStr){
		String desc = "";
		long timestamp = getTimestamp(dateStr);
		long diff = new Date().getTime() - timestamp;
		long days = diff/(24 * 60 * 60 * 1000);
		long hours = diff/(60 * 60 * 1000) - days*24;
		long mins = diff/(60 * 1000) - days*24*60 - hours*60;

		if(days == 0 && hours == 0 && mins < 10){
			desc = "刚刚";
		}
		else if(days == 0 && hours == 0 && mins >= 10){
			desc = mins + "分钟前";
		}
		else if(days == 0 && hours > 0){
			desc = hours + "小时前";
		}
		else if(days > 0 && days <= 30){
			desc = days + "天前";
		}
		else{
			desc = days/30 + "月前";
		}
		return desc;
	}
	
	public static String getYYMMDDFromYYMMDDHHMMSS(String date){
		long timeStamp = getTimestamp(date);
		return timestampToYMDay(timeStamp);
	}

	public static String getCurrYYMMDD() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(System.currentTimeMillis()));
	}
	
	public static String getUTCTimeString(){
		long timestamp = System.currentTimeMillis();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return format.format(new Date(timestamp));
	}
	
	public static String getUTCTimeString(long timeStamp){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return format.format(new Date(timeStamp));
	}

	// private static void testUTCTime(String format){
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
	// simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	// String date = simpleDateFormat.format(new Date());
	//
	// }
	
	public static long getDayTimestamp(String date){
		if(null == date){
			date = "1990-01-01";
		}
		DateFormat dateFormat = null;
		long time = 0;
		// "2013-11-29T02:55:03.000UTC"
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = dateFormat.parse(date);
			return d.getTime();
		} catch (ParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}

	public static Date getDate(String date) {
		// testUTCTime("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		// testUTCTime("yyyy-MM-dd'T'HH:mm:ss");
		Date d = null;
		if(null == date){
			return d;
		}

		DateFormat dateFormat = null;
		// "2013-11-29T02:55:03.000UTC"
		if (date.contains("T")) {
			date = date.replace("Z", "UTC");
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		} else {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		try {
			d = dateFormat.parse(date);
			return d;
		} catch (ParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		date = date.replace("UTC", "+0000");
		// date = date.replace("T", " ");
		// date = date.replace("###", "UTC");
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		try{
			d = dateFormat.parse(date);
			return d;
		}catch(ParseException e){
			e.printStackTrace();
		}
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
		try{
			d = dateFormat.parse(date);
			return d;
		}catch(ParseException e){
			e.printStackTrace();
		}
		return d;
	}

	public static long getTimestamp(String date) {
		if(RegexUtil.checkDigit(date)){
			return Long.parseLong(date);
		}

		Date d = getDate(date);
		if(null == d){
			return 0;
		}
		return d.getTime();
	}
	
	public static String timestampToDateTime(long timestamp) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(timestamp));
	}

	public static String timestampToDate(long timestamp) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(new Date(timestamp));
	}
	
	public static String timestampToMDHMDate(long timestamp) {
		DateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date(timestamp));
	}
	
	public static String timestampToYMDay(long timestamp) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(timestamp));
	}
	
	public static String timestampToMDay(long timestamp) {
		DateFormat format = new SimpleDateFormat("MM-dd");
		return format.format(new Date(timestamp));
	}

	public static String timestampToTime(long timestamp) {
		Calendar nowDate = Calendar.getInstance();
		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		nowDate.setTimeInMillis(timestamp);
		DateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(timestamp));
	}
	
//	public static boolean isNightTime(){
//		boolean ret = false;
//		Calendar nowDate = Calendar.getInstance();
//		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		nowDate.set(Calendar.HOUR_OF_DAY, 22);
//		nowDate.set(Calendar.MINUTE, 0);
//		nowDate.set(Calendar.SECOND, 0);
//		nowDate.set(Calendar.MILLISECOND, 0);
//		long tenHour = nowDate.getTimeInMillis();
//		long endDay = todayEnd();
//		long startDay = todayStart();
//		nowDate.set(Calendar.HOUR_OF_DAY, 6);
//		long sixHour = nowDate.getTimeInMillis();
//		long now = getCurrentTime();
//		if ((now >= startDay && now <= sixHour)
//				|| (now >= tenHour && now <= endDay)) {
//			ret = true;
//		}
//		return ret;
//	}
//
//	public static long getCurrentTime() {
//		Date date = new Date();
//		return date.getTime();
//	}
//
	private static int[] dayOfWeek = {6,0,1,2,3,4,5};
	private static String[] weeks = {"周一","周二","周三","周四","周五","周六","周日"};
	
	public static String getDayOfWeek(long timestamp) {
		int week = 0;
		Calendar nowDate = Calendar.getInstance();
		nowDate.setTimeInMillis(timestamp);
		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		week = nowDate.get(Calendar.DAY_OF_WEEK) - 1;
		week = dayOfWeek[week];
		return weeks[week];
	}
	
//	public static int getTodayOfWeek() {
//		int week = 0;
//		Calendar nowDate = Calendar.getInstance();
//		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		week = nowDate.get(Calendar.DAY_OF_WEEK) - 1;
//		week = dayOfWeek[week];
//		return week;
//	}
//
//	public static long todayStart() {
//		Calendar nowDate = Calendar.getInstance();
//		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		nowDate.set(Calendar.HOUR_OF_DAY, 0);
//		nowDate.set(Calendar.MINUTE, 0);
//		nowDate.set(Calendar.SECOND, 0);
//		nowDate.set(Calendar.MILLISECOND, 0);
//		return nowDate.getTimeInMillis();
//	}
//
//	public static long todayEnd() {
//		Calendar nowDate = Calendar.getInstance();
//		nowDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		nowDate.set(Calendar.HOUR_OF_DAY, 0);
//		nowDate.set(Calendar.MINUTE, 0);
//		nowDate.set(Calendar.SECOND, 0);
//		nowDate.set(Calendar.MILLISECOND, 0);
//		nowDate.add(Calendar.DAY_OF_MONTH, 1);
//		return nowDate.getTimeInMillis() - 1;
//	}
//
//	public static long[] dayOfWeekTimeRange(int i) {
//		int toDay = getTodayOfWeek();
//		long[] range = new long[2];
//
//		if (i > toDay) {
//			range[0] = todayEnd() + ((i - toDay - 1) * oneDayTimeValue);
//			range[1] = todayEnd() + ((i - toDay) * oneDayTimeValue);
//		} else if (i < toDay) {
//			range[0] = todayStart() - ((toDay - i) * oneDayTimeValue);
//			range[1] = todayStart() - ((toDay - i - 1) * oneDayTimeValue);
//		} else {
//			range[0] = todayStart();
//			range[1] = todayEnd();
//		}
//
//		return range;
//	}
//	
//	public static long[] dayOfWeekCurTimeRange(){
//		long[] range = new long[2];
//		range[0] = dayOfWeekTimeRange(0)[0];
//		range[1] = dayOfWeekTimeRange(7)[1];
//		return range;
//	}
//	
//	public static long[] dayOfMonthTimeRange(int i){
//		long[] range = new long[2];
//		range[0] = getMonthStartTime(i);
//		range[1] = getMonthEndTime(i);
//		return range;
//	}
//	
//	private static long getMonthStartTime(int i){
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		cal.set(Calendar.MONTH, i);
//		cal.set(Calendar.DAY_OF_MONTH,1);
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTimeInMillis();
//	}
//	
//	private static long getMonthEndTime(int i){
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//		cal.set(Calendar.MONTH, i + 1);
//		cal.set(Calendar.DAY_OF_MONTH,1);
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		return cal.getTimeInMillis();
//	}

	public static String convert(int val) {
		String retStr = "";
		switch (val) {
		case 0:
			return "星期日";
		case 1:
			return "星期一";
		case 2:
			return "星期二";
		case 3:
			return "星期三";
		case 4:
			return "星期四";
		case 5:
			return "星期五";
		case 6:
			return "星期六";
		default:
			break;
		}
		return retStr;
	}
}
