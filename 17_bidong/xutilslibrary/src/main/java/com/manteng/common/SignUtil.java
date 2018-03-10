package com.manteng.common;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.NameValuePair;

public class SignUtil {
	public static String makeSign(String masterSecret,
			Map<String, Object> params) throws IllegalArgumentException {
		if ((masterSecret == null) || (params == null)) {
			throw new IllegalArgumentException(
					"masterSecret and params can not be null.");
		}

		if (!(params instanceof SortedMap)) {
			params = new TreeMap<String, Object>(params);
		}

		StringBuilder input = new StringBuilder(masterSecret);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if (((value instanceof String)) || ((value instanceof Integer))
					|| ((value instanceof Long))) {
				input.append((String) entry.getKey());
				input.append(entry.getValue());
			}
		}

		return MD5Util.getMD5Format(input.toString());
	}

	public static String makeSignForString(String masterSecret,
			List<NameValuePair> params) throws IllegalArgumentException {
		if ((masterSecret == null) || (params == null)) {
			throw new IllegalArgumentException(
					"masterSecret and params can not be null.");
		}
		TreeMap<String,String> map = new TreeMap<String, String>();
		for(NameValuePair pair : params){
			map.put(pair.getName(), pair.getValue());
		}
		StringBuilder input = new StringBuilder(masterSecret);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			input.append(entry.getKey());
			input.append(entry.getValue());
		}
//		LogUtil.i("SIGN", input.toString());
		return MD5Util.getMD5Format(input.toString());
	}
}