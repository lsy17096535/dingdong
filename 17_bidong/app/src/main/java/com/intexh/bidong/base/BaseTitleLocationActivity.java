package com.intexh.bidong.base;

import com.intexh.bidong.location.LocationHelper;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.location.LocationHelper.OnLocationListener;

public abstract class BaseTitleLocationActivity extends BaseTitleActivity {

	protected LocationInfo locationInfo = null;
	protected abstract void didUpdateLocation(LocationInfo info);
	
	private OnLocationListener mLocationListener = new OnLocationListener() {
		
		@Override
		public void onLocationChange(LocationInfo locationInfo) {
			BaseTitleLocationActivity.this.locationInfo = locationInfo;
			didUpdateLocation(locationInfo);
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocationHelper.getInstance(this).destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocationHelper.getInstance(this).stopLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationHelper.getInstance(this).startLocation(mLocationListener);;
	}
	
	
}
