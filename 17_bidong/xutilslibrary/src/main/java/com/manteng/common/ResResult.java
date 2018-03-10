package com.manteng.common;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.util.LogUtils;

public class ResResult{
	private int code;
	private String reason;
	private String err;
	private String data;
	
	public ResResult(int code, String reason, String data, String err){
		this.code = code;
		this.reason = reason;
		this.data = data;
		this.err = err;
	}
	
	public int getCode() {
		return code;
	}

	public String getReason() {
		return reason;
	}

	public String getErr() {
		return err;
	}

	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public static ResResult parse(String jsonStr){
		 try {  			    
			    JSONObject result = new JSONObject(jsonStr.replace("\n", "")); 
			    int code =  -1;
			    String reason = null;
			    String err = null;
			    String data = null;
			    if(result.has("code")){
			    	code = result.getInt("code");
			    }
			    if(result.has("reason")){
			    	reason = result.getString("reason");  
			    }
			    if(result.has("err")){
			    	err = result.getString("err");  
			    }
			    if(result.has("data")){
			    	data = result.getString("data");
			    }
			    return new ResResult(code, reason, data, err);
			} catch (JSONException ex) {  
				LogUtils.e(jsonStr,ex);
			    return null;
			}  
	}
}
