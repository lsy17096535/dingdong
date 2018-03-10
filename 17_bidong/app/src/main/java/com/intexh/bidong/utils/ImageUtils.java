package com.intexh.bidong.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import com.intexh.bidong.R;

public class ImageUtils {


	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		if(null == uri){
			return null;
		}
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

//	private static final String PIC_BASE_URL = "http://ppstar.image.alimmdn.com";
//	private static final String VIDEO_BASE_URL = "http://ppstar.file.alimmdn.com";
	private static DisplayImageOptions avatarOpt = null;
	private static DisplayImageOptions avatarCornerOpt = null;

	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }
	
	public static void loadGiftImage(String fileName,ImageView imageView){
		if(null == fileName || null == imageView){
			return ;
		}
		imageView.setImageResource(R.drawable.selector_giftbk);
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getAvatarBucketThumbUrl(fileName, 0.5f), imageView);
		}
	}
	
	// Warning! don't call this function in ListAdapter
	public static Bitmap scaleToSize(Bitmap src, int width, int height) {
		if (src == null)
			return null;

		float w = (float) width / src.getWidth();
		float h = (float) height / src.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(w, h);
		Bitmap target = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
		// Modified by fubin, 2012-3-27
		// return weak reference of bitmap
		WeakReference<Bitmap> wr = new WeakReference<Bitmap>(target);
		return wr.get();
	}
	
	/**
	 * 缩小、压缩并保存
	 * @param src 原图片
	 * @param fileName 文件名
	 * @param path 路径
	 * @param act Context
	 * @return 保存是否成功
	 */
	public static boolean scaleSave(Bitmap src, String fileName, String path, Activity act) {
		Bitmap tmp = src;
		@SuppressWarnings("deprecation")
		int screenWidth = act.getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		@SuppressWarnings("deprecation")
		int screenHeight = act.getWindowManager().getDefaultDisplay().getHeight();
		int sWidth = src.getWidth();
		int sHeight = src.getHeight();
		if(sWidth > screenWidth || sHeight > screenHeight){
			tmp = scaleToSize(src, screenWidth, screenHeight);
		}		
		try {
			File file = FileUtils.createFile(path, fileName);
			FileOutputStream fout = new FileOutputStream(file);
			tmp.compress(Bitmap.CompressFormat.JPEG, 70, fout);
			fout.flush();
			fout.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			tmp = null;
		}	   
	}	
	
	public static Bitmap resampleImageEx(String path, int maxDim) throws Exception {
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, bfo);
		BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
		int scaleMax = 0;
		if(bfo.outHeight > bfo.outWidth){
			scaleMax = bfo.outHeight;
		}else{
			scaleMax = bfo.outWidth;
		}
		if(scaleMax > 2000){
			optsDownSample.inSampleSize = scaleMax/1000;
		}else{
			optsDownSample.inSampleSize = 1;
		}
		Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);
		Matrix m = new Matrix();
		// if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
		BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(),
				bmpt.getHeight(), maxDim);
		m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
				(float) optsScale.outHeight / (float) bmpt.getHeight());
		// }

		int sdk = new Integer(Build.VERSION.SDK).intValue();
		if (sdk > 4) {
			int rotation = ExifUtils.getExifRotation(path);
			if (rotation != 0) {
				m.postRotate(rotation);
			}
		}
		Bitmap ret = Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(),
				bmpt.getHeight(), m, true);
		if (ret != bmpt) {// CreateBitmap可能会和原图一致。链接：http://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap%28android.graphics.Bitmap,%20int,%20int,%20int,%20int%29
			if (null != bmpt && !bmpt.isRecycled()) {
				bmpt.recycle();
			}
		}
		return ret;
	}
	
	private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
		float scaleVal = 1.0f;
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		if (cx > cy) {
			scaleVal = (float) max / (float) cx;
		} else if (cy > cx) {
			scaleVal = (float) max / (float) cy;
		} else {
			scaleVal = (float) max / (float) cx;
		}
		bfo.outWidth = (int) (cx * scaleVal + 0.5f);
		bfo.outHeight = (int) (cy * scaleVal + 0.5f);
		return bfo;
	}
	
	private static void initAvatarImageLoaderIfNeed(){
		if(null == avatarOpt){
			DisplayImageOptions.Builder opt = new DisplayImageOptions.Builder();
			opt.resetViewBeforeLoading(true)
			.showImageOnLoading(R.drawable.img_loading)
			.showImageForEmptyUri(R.drawable.img_loading)
			.showImageOnFail(R.drawable.img_loading)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565);
			avatarOpt = opt.build();
		}
	}

	private static void initAvatarCornerImageLoaderIfNeed(int size){
		if(null == avatarCornerOpt){
			DisplayImageOptions.Builder opt = new DisplayImageOptions.Builder();
			opt.resetViewBeforeLoading(true)
			.showImageOnLoading(R.drawable.img_loading)
			.showImageForEmptyUri(R.drawable.img_loading)
			.showImageOnFail(R.drawable.img_loading)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(size));
			avatarCornerOpt = opt.build();
		}
	}

	private static ImageLoadingListener avatarLoadingListener = new ImageLoadingListener() {
		
		@Override
		public void onLoadingStarted(String url, View arg1) {
			if(arg1 instanceof ImageView){
				ImageView imageView = (ImageView)arg1;
				String realUrl = (String)(arg1.getTag(R.id.tag_first));
				if(null != url && url.equals(realUrl)){
					imageView.setImageResource(R.drawable.pic_photo_a);
				}
			}
		}
		
		@Override
		public void onLoadingFailed(String url, View arg1, FailReason arg2) {
			if(arg1 instanceof ImageView){
				ImageView imageView = (ImageView)arg1;
				String realUrl = (String)(arg1.getTag(R.id.tag_first));
				if(null != url && url.equals(realUrl)){
					imageView.setImageResource(R.drawable.pic_photo_a);
				}
			}
		}
		
		@Override
		public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
			if(arg1 instanceof ImageView){
				ImageView imageView = (ImageView)arg1;
				String realUrl = (String)(arg1.getTag(R.id.tag_first));
				if(null != url && url.equals(realUrl)){
					if(null != arg2){
						imageView.setImageBitmap(arg2);
					}else{
						imageView.setImageResource(R.drawable.pic_photo_a);
					}
				}
			}
		}
		
		@Override
		public void onLoadingCancelled(String url, View arg1) {
			if(arg1 instanceof ImageView){
				ImageView imageView = (ImageView)arg1;
				String realUrl = (String)(arg1.getTag(R.id.tag_first));
				if(null != url && url.equals(realUrl)){
					imageView.setImageResource(R.drawable.pic_photo_a);
				}
			}
		}
	};

	public static void loadWorksDefaultImage(String fileName,ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		imageView.setImageResource(R.drawable.img_loading);
		String url = fileName;
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			imageView.setTag(R.id.tag_first, fileName);
			ImageLoader.getInstance().displayImage(fileName, imageView,avatarOpt);
		}else{
			url = BucketHelper.getInstance().getWorksBucketThumbUrl(fileName, 1.0f);
			imageView.setTag(R.id.tag_first,url);
			ImageLoader.getInstance().displayImage(url, imageView,avatarOpt);
		}
	}

	public static void loadAvatarDefaultImage(String fileName, ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		initAvatarImageLoaderIfNeed();
		imageView.setImageResource(R.drawable.img_loading);
		String url = fileName;
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			imageView.setTag(R.id.tag_first, fileName);
			ImageLoader.getInstance().displayImage(fileName, imageView,avatarOpt);
		}else{
			url = BucketHelper.getInstance().getAvatarBucketThumbUrl(fileName, 1.0f);
			imageView.setTag(R.id.tag_first,url);
			ImageLoader.getInstance().displayImage(url, imageView,avatarOpt);
		}
	}
	
	public static void loadMiddleAvatarImage(String fileName,ImageView imageView,float ratio){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		initAvatarImageLoaderIfNeed();
		imageView.setImageResource(R.drawable.img_loading);
		String url = fileName;
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			imageView.setTag(R.id.tag_first, fileName);
			ImageLoader.getInstance().displayImage(fileName, imageView,avatarOpt);
		}else{
			url = BucketHelper.getInstance().getMidlleAvatarBucketThumbUrl(fileName, 1.0f);
			imageView.setTag(R.id.tag_first,url);
			ImageLoader.getInstance().displayImage(url, imageView,avatarOpt);
		}
	}

	//加载切角
	public static void loadMiddleCornerAvatarImage(String fileName,int cornerSize,ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		initAvatarCornerImageLoaderIfNeed(cornerSize);
		imageView.setImageResource(R.drawable.img_loading);
		String url = fileName;
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			imageView.setTag(R.id.tag_first, fileName);
			ImageLoader.getInstance().displayImage(fileName, imageView,avatarCornerOpt);
		}else{
			url = BucketHelper.getInstance().getMidlleAvatarBucketThumbUrl(fileName, 1.0f);
			imageView.setTag(R.id.tag_first,url);
			ImageLoader.getInstance().displayImage(url, imageView,avatarCornerOpt);
		}
	}

	public static void loadBigAvatarImage(String fileName,final ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		imageView.setImageResource(R.drawable.img_loading);
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getBigAvatarBucketFullUrl(fileName), imageView);
		}
	}

	public static void loadOffServiceItemImage(String fileName,final ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		if(fileName.startsWith("http") || fileName.startsWith("file") || fileName.startsWith("drawable")){
			if(fileName.startsWith("http")){
				imageView.setImageResource(R.drawable.img_loading);
			}
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			imageView.setImageResource(R.drawable.img_loading);
		}
	}
	
	public static void loadSnapshotDefaultImage(String fileName,final ImageView imageView){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
		imageView.setImageResource(R.drawable.img_loading);
		if(fileName.startsWith("http") || fileName.startsWith("file")){
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getSnapshotBucketThumbUrl(fileName, 1.0f), imageView);
		}
	}

	public static void loadWorksImage(String fileName,final ImageView imageView,float ratio){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(fileName.startsWith("http") || fileName.startsWith("file") || fileName.startsWith("drawable")){
			if(fileName.startsWith("http")){
				imageView.setImageResource(R.drawable.img_loading);
			}
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			imageView.setImageResource(R.drawable.img_loading);
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getWorksBucketThumbUrl(fileName, ratio), imageView);
		}
	}

	public static void loadSnapshotImage(String fileName,final ImageView imageView,float ratio){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(fileName.startsWith("http") || fileName.startsWith("file") || fileName.startsWith("drawable")){
			if(fileName.startsWith("http")){
				imageView.setImageResource(R.drawable.img_loading);
			}
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			imageView.setImageResource(R.drawable.img_loading);
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getSnapshotBucketThumbUrl(fileName, ratio), imageView);
		}
	}

	public static void loadSnapshotSmallImage(String fileName,final ImageView imageView,float ratio){
		if(null == fileName || null == imageView){
			if(null != imageView){
				imageView.setImageResource(R.drawable.img_loading);
			}
			return ;
		}
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if(fileName.startsWith("http") || fileName.startsWith("file") || fileName.startsWith("drawable")){
			if(fileName.startsWith("http")){
				imageView.setImageResource(R.drawable.img_loading);
			}
			ImageLoader.getInstance().displayImage(fileName, imageView);
		}else{
			imageView.setImageResource(R.drawable.img_loading);
			ImageLoader.getInstance().displayImage(BucketHelper.getInstance().getSnapshotBucketSmallThumbUrl(fileName, ratio), imageView);
		}
	}


	/**
	 * 缩小、压缩并保存
	 * @param src 原图片
	 * @param fileName 保存路径
	 * @param act Context
	 * @return 保存是否成功
	 */
	public static boolean saveBitmapToDisk(Bitmap src, String fileName, Activity act) {
		Bitmap tmp = src;
		try {
			File file = new File(fileName);
			FileOutputStream fout = new FileOutputStream(file);
			tmp.compress(Bitmap.CompressFormat.JPEG, 70, fout);
			fout.flush();
			fout.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			tmp = null;
		}	   
	}	
}
