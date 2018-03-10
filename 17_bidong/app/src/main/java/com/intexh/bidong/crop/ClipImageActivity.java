package com.intexh.bidong.crop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.intexh.bidong.R;
import com.intexh.bidong.utils.FileUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 裁剪图片的Activity
 */
public class ClipImageActivity<T> extends Activity implements OnClickListener {
	public static final String RESULT_PATH = "crop_image";
	public static final String PATH_KEY = "path";
	private ClipImageLayout mClipImageLayout = null;

	public static final int CROP_RESULT_CODE = 50000;
	public static final int START_ALBUM_REQUESTCODE = CROP_RESULT_CODE + 1;
	public static final int CAMERA_WITH_DATA = CROP_RESULT_CODE + 2;

	public static final String TMP_FILENAME = "ppstar_tmp.jpg";
	public static final String TMP_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
		 + File.separator + TMP_FILENAME;

	public static void startActivity(Activity activity, String path) {
		Intent intent = new Intent(activity, ClipImageActivity.class);
		intent.putExtra(PATH_KEY, path);
		activity.startActivityForResult(intent, CROP_RESULT_CODE);
	}

	public static void startActivity(Fragment fragment, String path) {
		Intent intent = new Intent(fragment.getActivity(), ClipImageActivity.class);
		intent.putExtra(PATH_KEY, path);
		fragment.startActivityForResult(intent, CROP_RESULT_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_image_layout);

		mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
		String path = getIntent().getStringExtra(PATH_KEY);

		// 有的系统返回的图片是旋转了，有的没有旋转，所以处理
		int degreee = readBitmapDegree(path);
		Bitmap bitmap = createBitmap(path);
		if (bitmap != null) {
			mClipImageLayout.setImageBitmap(bitmap);
			if (degreee == 0) {
				mClipImageLayout.setImageBitmap(bitmap);
			} else {
				mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
			}
		} else {
			finish();
		}
		findViewById(R.id.okBtn).setOnClickListener(this);
		findViewById(R.id.cancleBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.okBtn) {
			String path = getExternalCacheDir().getAbsolutePath()
					+ File.separator + FileUtils.genPicFileName();
			Bitmap bitmap = mClipImageLayout.clip();
			saveBitmap(bitmap, path);

			Intent intent = new Intent();
			intent.putExtra(RESULT_PATH, path);
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	private void saveBitmap(Bitmap bitmap, String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}

		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
			fOut.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建图片
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap createBitmap(String path) {
		if (path == null) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		FileInputStream is = null;
		Bitmap bitmap = null;
		try {
			is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	// 读取图像的旋转度
	private int readBitmapDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转图片
	private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return resizedBitmap;
	}

	/**
	 * 通过uri获取文件路径
	 *
	 * @param mUri
	 * @return
	 */
	public static String getFilePath(Activity t, Uri mUri) {
		if (mUri.getScheme().equals("file")) {
			return mUri.getPath();
		} else {
			Cursor cursor = t.getContentResolver().query(mUri, null, null, null, null);
			cursor.moveToFirst();
			return cursor.getString(1);
		}
	}

	// 裁剪图片的Activity
	public static void startCropImageActivity(Activity t, String path) {
		startActivity(t, path);
	}

	// 裁剪图片的Activity
	public static void startCropImageActivity(Activity t, Uri uri) {
		startCropImageActivity(t, getFilePath(t, uri));
	}

	// 裁剪图片的Activity
	public static void startCropImageActivity(Fragment t, String path) {
		startActivity(t, path);
	}

	// 裁剪图片的Activity
	public static void startCropImageActivity(Fragment t, Uri uri) {
		startCropImageActivity(t, getFilePath(t.getActivity(), uri));
	}

	//使用MultiImageSelector库选择图片，避免android不同版本取相册图片的差异
	public static void starImageSelector(Activity activity){
		Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
		intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
		activity.startActivityForResult(intent, START_ALBUM_REQUESTCODE);
	}

	public static void startAlbum(Fragment t) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			t.startActivityForResult(intent, START_ALBUM_REQUESTCODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				t.startActivityForResult(intent, START_ALBUM_REQUESTCODE);
			} catch (Exception e2) {
				e.printStackTrace();
			}
		}
	}

	public static void startAlbum(Activity t) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			t.startActivityForResult(intent, START_ALBUM_REQUESTCODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				t.startActivityForResult(intent, START_ALBUM_REQUESTCODE);
			} catch (Exception e2) {
				e.printStackTrace();
			}
		}
	}

	public static void startCapture(Fragment t) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TMP_PATH)));
		t.startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	public static void startCapture(Activity t) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(TMP_PATH)));
		t.startActivityForResult(intent, CAMERA_WITH_DATA);
	}
}
