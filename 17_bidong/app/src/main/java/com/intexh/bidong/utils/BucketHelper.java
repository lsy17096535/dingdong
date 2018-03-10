package com.intexh.bidong.utils;

import java.util.HashMap;
import java.util.Map;

import com.intexh.bidong.userentity.BucketEntity;
import com.intexh.bidong.userentity.RegDataEntity;

public class BucketHelper {

	public static final String AVATAR_NAMESPACE = "bdavatar";
	public static final String VIDEO_NAMESPACE = "bdvideo";
	public static final String WORKS_NAMESPACE = "bdwork";
	private static BucketHelper mHelper = null;
	private Map<String, BucketEntity> mMaps = null;

	private BucketEntity getBucketByName(String bucketName){
		BucketEntity ret = null;
		loadBucketMapIfNeeded();
		if(null != mMaps){
			ret = mMaps.get(bucketName);
		}
		return ret;
	}

	private void loadBucketMapIfNeeded(){
		if(null == mMaps || mMaps.keySet().size() == 0){
			RegDataEntity userInfo = UserUtils.getUserInfo();
			BucketEntity[] buckets = userInfo.getBuckets();
			if(null != buckets){
				mMaps = new HashMap<>();
				for(BucketEntity entity : buckets){
					mMaps.put(entity.getBucketName(), entity);
				}
			}
		}
	}

	public String getWorksBucketToken(){
		String ret = null;
		BucketEntity entity = getBucketByName(WORKS_NAMESPACE);
		if(null != entity){
			ret = entity.getToken();
		}
		return ret;
	}

	public String getAvatarBucketToken(){
		String ret = null;
		BucketEntity entity = getBucketByName(AVATAR_NAMESPACE);
		if(null != entity){
			ret = entity.getToken();
		}
		return ret;
	}

	public String getVideoBucketToken(){
		String ret = null;
		BucketEntity entity = getBucketByName(VIDEO_NAMESPACE);
		if(null != entity){
			ret = entity.getToken();
		}
		return ret;
	}

	public String getWorksBucketFullUrl(String url){
		String ret = null;
		BucketEntity entity = getBucketByName(WORKS_NAMESPACE);
		if(null != entity){
			ret = entity.getImageUrl() + url;
		}
		return ret;
	}

	public String getAvatarBucketFullUrl(String url){
		String ret = null;
		BucketEntity entity = getBucketByName(AVATAR_NAMESPACE);
		if(null != entity){
			ret = entity.getImageUrl() + url;
		}
		return ret;
	}
	
	public String getVideoBucketFullUrl(String url){
		String ret = null;
		BucketEntity entity = getBucketByName(VIDEO_NAMESPACE);
		if(null != entity){
			ret = entity.getImageUrl() + url;
		}
		return ret;
	}


	public String getAvatarBucketSquareThumbUrl(String url){
		return getAvatarBucketThumbUrl(url, 1.0f);
	}

	public String getWorksBucketSquareThumbUrl(String url){
		return getWorksBucketThumbUrl(url, 1.0f);
	}

	public String getWorksBucketThumbUrl(String url,float ratio){
		String ret = getBucketThumbUrl(url,ratio,WORKS_NAMESPACE);
		return ret;
	}
	
	private String getBucketThumbUrl(String url,float ratio,String bucketName){
		String ret = null;
		BucketEntity entity = getBucketByName(bucketName);
		if(null != entity){
			ret = entity.getImageUrl() + url + "@" + entity.getThumbnail()*2 + "w_" + (int)(entity.getThumbnail()*2*ratio)+"h_90Q.jpg";
		}
		return ret;
	}

	private String getBucketMiddleThumbUrl(String url,float ratio,String bucketName){
		String ret = null;
		BucketEntity entity = getBucketByName(bucketName);
		if(null != entity){
			ret = entity.getImageUrl() + url + "@" + entity.getThumbnail()*3 + "w_" + (int)(entity.getThumbnail()*3*ratio)+"h_90Q_1e.jpg";
		}
		return ret;
	}
	
	private String getBucketBigThumbUrl(String url,float ratio,String bucketName){
		String ret = null;
		BucketEntity entity = getBucketByName(bucketName);
		if(null != entity){
			ret = entity.getImageUrl() + url + "@" + entity.getThumbnail()*5 + "w_" + (int)(entity.getThumbnail()*5*ratio)+"h_90Q_1e.jpg";
		}
		return ret;
	}

	public String getAvatarBucketThumbUrl(String url,float ratio){
		String ret = getBucketThumbUrl(url,ratio,AVATAR_NAMESPACE);
		return ret;
	}

	public String getMidlleAvatarBucketThumbUrl(String url,float ratio){
		String ret = getBucketMiddleThumbUrl(url,ratio, AVATAR_NAMESPACE);
		return ret;
	}

	public String getBigAvatarBucketFullUrl(String url){
		String ret = null;
		BucketEntity entity = getBucketByName(AVATAR_NAMESPACE);
		if(null != entity){
			ret = entity.getImageUrl() + url;
		}
		return ret;
	}
	
	public String getSnapshotBucketFullUrl(String url){
		String ret = null;
		BucketEntity entity = getBucketByName(VIDEO_NAMESPACE);
		if(null != entity){
			ret = entity.getImageUrl() + url;
		}
		return ret;
	}

	public String getSnapshotBucketSmallThumbUrl(String url, float ratio){
		String ret = getBucketThumbUrl(url, ratio, VIDEO_NAMESPACE);
		return ret;
	}
	
	public String getSnapshotBucketSquareThumbUrl(String url){
		return getSnapshotBucketThumbUrl(url, 1.0f);
	}
	
	public String getSnapshotBucketThumbUrl(String url,float ratio){
		String ret = getBucketBigThumbUrl(url,ratio,VIDEO_NAMESPACE);
		return ret;
	}
	
	private BucketHelper(){
		super();
	}
	
	public static BucketHelper getInstance(){
		if(null == mHelper){
			mHelper = new BucketHelper();
		}
		return mHelper;
	}
	
}
