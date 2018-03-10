package com.intexh.bidong.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {
	/**
	 * dip值转px值
	 * @param context
	 * @param dpValue dip值
     * @return
     */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px值转dip值
	 * @param context
	 * @param pxValue 像素值
	 *
     * @return
     */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/** 获取手机的密度*/
	public static float getDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
}
