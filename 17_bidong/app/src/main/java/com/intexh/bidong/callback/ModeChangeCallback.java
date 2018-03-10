package com.intexh.bidong.callback;

import com.intexh.bidong.location.LocationHelper.LocationInfo;

public interface ModeChangeCallback<T> {
	public void onModeChange(int mode,T data);
	public void onLocationChange(LocationInfo locationInfo);
}
