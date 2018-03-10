package com.intexh.bidong.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.UUID;

public class DeviceUtil {

    private static final String MATCHER_TEST_KEY = "test-keys";
    private static final String MATCHER_RELEASE_KEY = "release-keys";
    private static final String MATCHER_SDK = "sdk";

    /**
     * Check if the device is emulator
     * 
     * @return
     */
    public static boolean isEmulator() {
        boolean isEmulator = false;

        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String tags = Build.TAGS;
        String fingerprint = Build.FINGERPRINT;

		isEmulator = model.contains(MATCHER_SDK)
				|| product.contains(MATCHER_SDK)
				|| tags.contains(MATCHER_TEST_KEY)
				|| !tags.contains(MATCHER_RELEASE_KEY)
				|| fingerprint.contains(MATCHER_TEST_KEY)
				|| !fingerprint.contains(MATCHER_RELEASE_KEY);
		return isEmulator;
	}

    /**
     * Get the random uuid
     * @return
     */
    public static String getUUID() {
		final UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

	/**
	 * Get the imei
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceImei(Context context) {
		String imei = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imei = tm.getDeviceId();
		}
		return imei;
	}

    /**
     * Get the manufacturer info
     * 
     * @return
     */
    public static String getManufacturerInfo() {
        return System.getProperty("ro.product.manufacturer");
    }
    
    /**
     * Get the OS Version
     * 
     * @param context
     * @return
     */
    public static String getOSVersion(Context context){
    	String version = Build.VERSION.RELEASE;
        return version;
    }
    
    /**
     * Get the User-Agent
     * @param context
     * @return
     */
    public static String getUA(Context context){
    	WebView webview;
    	webview = new WebView(context);
    	webview.layout(0, 0, 0, 0);
    	WebSettings settings = webview.getSettings();
    	String ua = settings.getUserAgentString();
    	webview = null;
    	return ua;
    }   
    
}