package com.intexh.bidong.utils;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.location.LocationLocalManager;

public final class StringUtil {

	public static final String CHARSET_NAME_UTF8 = "UTF-8";

	private static final char[] digital = "0123456789ABCDEF".toCharArray();


//	/**
//	 * format the file size in KB or MB
//	 * 
//	 * @param size
//	 * @return
//	 */
//	public static String formatFileSize(long size) {
//		StringBuilder sb = new StringBuilder();
//		long k = size / 1024;
//		if (k > 1024) {
//			long m = k / 1024;
//			sb.append(m);
//			sb.append("M");
//		} else {
//			sb.append(k);
//			sb.append("K");
//		}
//		return sb.toString();
//	}
//
//	/**
//	 * Compare the new version with the old version If the new version is bigger
//	 * than the old version, return true, else return false
//	 * 
//	 * eg. newVersion = 1.10.2 oldVersion = 1.2.10
//	 * 
//	 * @param newVersion
//	 * @param oldVersion
//	 * @return
//	 */
//	public static boolean compareVersion(String newVersion, String oldVersion) {
//		if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(oldVersion))
//			return false;
//
//		String[] newVersions = newVersion.split(".");
//		String[] oldVersions = oldVersion.split(".");
//		if (newVersion.length() <= 0
//				|| newVersion.length() != oldVersion.length())
//			return false;
//
//		for (int i = 0; i < newVersions.length; i++) {
//			if (Integer.parseInt(newVersions[i]) > Integer
//					.parseInt(oldVersions[i]))
//				return true;
//		}
//
//		return false;
//	}
//
//	/**
//	 * Encode the byte array to the hex string
//	 * 
//	 * @param bytes
//	 * @return
//	 */
//	public static String encodeHexStr(final byte[] bytes) {
//		if (bytes == null) {
//			return null;
//		}
//		char[] result = new char[bytes.length * 2];
//		for (int i = 0; i < bytes.length; i++) {
//			result[i * 2] = digital[(bytes[i] & 0xf0) >> 4];
//			result[i * 2 + 1] = digital[bytes[i] & 0x0f];
//		}
//		return new String(result);
//	}

	public static String getDistanceStr(double[] location){
		String ret = "";
		if(null == location){
			return ret;
		}
		LatLng p1 = new LatLng(location[1], location[0]);
		LocationInfo locationInfo = LocationLocalManager.getInstance().getLastLocation();
		if(null == locationInfo){
			ret = "未知距离";
		}else{
			LatLng p2 = LocationLocalManager.getInstance().getLastLocation().location;
			double distance = DistanceUtil.getDistance(p1, p2);
			ret = getDistanceShortStr(distance);
		}
		return ret;
	}

	public static String getDistanceShortStr(double distance){
		String ret = "";
		if(distance < 1000){
			ret = (int)distance + "m";
		}else{
			ret = (int)(distance/1000) + "km";
		}
		return ret;
	}

	public static double[] getLocPoint(String loc){
		if(null == loc){
			return null;
		}
		int startIndex = loc.indexOf("(") + 1;
		int endIndex = loc.lastIndexOf(")");
		String[] tmpPoint = loc.substring(startIndex, endIndex).split(" ");
		return new double[]{Double.valueOf(tmpPoint[0]), Double.valueOf(tmpPoint[1])};
	}
	
//	/**
//	 * Convert the string to the byte array with the specified encoding
//	 * 
//	 * @param str
//	 * @param encoding
//	 * @return
//	 */
//	public static byte[] toBytes(final String str, String encoding) {
//		if (str == null) {
//			return null;
//		}
//
//		try {
//			return str.getBytes(encoding);
//		} catch (final UnsupportedEncodingException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
//	}
//
//	public static byte[] toBytes(final String str) {
//		if (str == null) {
//			return null;
//		}
//		try {
//			return str.getBytes(CHARSET_NAME_UTF8);
//		} catch (final UnsupportedEncodingException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
//	}
	
	public static boolean isEmptyString(String ss){
		boolean ret = false;
		if(null == ss || "".equals(ss)){
			ret = true;
		}
		return ret;
	}

	public static String getGenderStr(int gender,Context context){
		String ret = context.getResources().getString(R.string.male_flag);
		if(2 == gender){
			ret = context.getResources().getString(R.string.femal_flag);
		}
		return ret;
	}

	public static String reverse(String value, String regex) {
		String[] list = value.split(regex);
		StringBuilder builder = new StringBuilder();
		builder.append(list[list.length - 1]);

		for(int i = list.length - 2; i >= 0; --i) {
			builder.append('.');
			builder.append(list[i]);
		}

		return builder.toString();
	}

	public static List<String> splitStr(String str, char delimeter) {
		int lastIndex = str.lastIndexOf(delimeter);
		char[] ch = str.toCharArray();
		ArrayList list = new ArrayList();
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < ch.length; ++i) {
			if(ch[i] != delimeter) {
				sb.append(ch[i]);
				if(i == ch.length - 1) {
					list.add(sb.toString());
				}
			} else {
				list.add(sb.toString());
				sb = new StringBuffer();
				if(i > lastIndex) {
					sb.append(ch[i]);
				}
			}
		}

		return list;
	}

	public static String join(String sep, String[] data) {
		if(null != data && data.length > 0) {
			StringBuilder builder = new StringBuilder(data[0]);
			for(int i = 1; i < data.length; ++i) {
				builder.append(sep);
				builder.append(data[i]);
			}

			return builder.toString();
		} else {
			return "";
		}
	}

	public static boolean contain(String item, String[] data) {
		boolean rs = false;
		if(null != item && null != data && data.length > 0) {
			for(int i = 0; i < data.length; i++) {
				if(item.equals(data[i])){
					rs = true;
					break;
				}
			}
		}
		return rs;
	}
	
}