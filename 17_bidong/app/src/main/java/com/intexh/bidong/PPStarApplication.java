package com.intexh.bidong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.multidex.MultiDexApplication;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.WantuService;
import com.baidu.mapapi.SDKInitializer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.intexh.bidong.net.NetworkManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.DeviceUtil;
import com.intexh.bidong.utils.FileUtils;
import com.intexh.bidong.utils.VersionManager;

import cn.sharesdk.framework.ShareSDK;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import static com.alibaba.sdk.android.media.core.MediaContext.context;


public class PPStarApplication extends MultiDexApplication {
	private static final String LOG_TAG = "VideoCache";
	private static HttpProxyCacheServer cacheProxy;
	public static Context applicationContext;
	private static PPStarApplication instance;
	public static WantuService wantuService;	//图片服务

	public static PPStarApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		applicationContext = this;
		instance = this;
		// init demo helper
		DemoHelper.getInstance().init(applicationContext);
		PtrLocalDisplay.init(this);
		LocationLocalManager.init(this);
		FriendsManager.init(this);
		LogUtils.allowD = true;
		SDKInitializer.initialize(this);
		VersionManager.init(this);
		HttpUtils.VERSION_CODE = VersionManager.getInstance().getVersionCode();
		HttpUtils.VERSION_NAME = VersionManager.getInstance().getVersionName();
		HttpUtils.DEVICE_IMEI = DeviceUtil.getDeviceImei(this);

		//init image loader
		initImageLoader();
		ShareSDK.initSDK(this);
		//初始化百川玩兔Service
		initWantuService();
		NetworkManager.INSTANCE.init(this); //初始化网络请求
		//集成趣拍必须要做的初始化
		QupaiHelper.initQupai();

	}

	private void showWindow() {
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundColor(Color.BLACK);
		textView.setText("随行科技");
		textView.setTextSize(10);
		textView.setTextColor(Color.RED);
		//类型是TYPE_TOAST，像一个普通的Android Toast一样。这样就不需要申请悬浮窗权限了。
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
		//初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = 500;
		params.height = 500;
		params.gravity=Gravity.CENTER;
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
		windowManager.addView(textView, params);
	}

	public void initWantuService() {
		/*############# new. 完成多媒体服务的异步初始化##############*/
		WantuService.enableHttpDNS(); // 可选。为了避免域名劫持，建议开发者启用HttpDNS
		WantuService.enableLog(); //在调试时，可以打开日志。正式上线前可以关闭

		wantuService = WantuService.getInstance();
		wantuService.asyncInit(this);
	}

	public void initImageLoader(){
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();

		File cacheDir = StorageUtils.getCacheDirectory(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.threadPoolSize(10) // default
				.threadPriority(Thread.NORM_PRIORITY - 2) // default
				.tasksProcessingOrder(QueueProcessingType.FIFO) // default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(5 * 1024 * 1024))
				.memoryCacheSize(5 * 1024 * 1024)
				.memoryCacheSizePercentage(13) // default
				.diskCache(new UnlimitedDiskCache(cacheDir)) // default
				.diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileCount(500)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(this)) // default
				.imageDecoder(new BaseImageDecoder(true)) // default
				.writeDebugLogs()
				.defaultDisplayImageOptions(defaultOptions)
				.build();

		ImageLoader.getInstance().init(config);
	}

	//取视频缓存url
	public String getProxyUrl(String orgUri, Activity activity){
		if(null == orgUri){
			return null;
		}
		String playUrl = null;
		if(FileUtils.isExists(orgUri)){
			playUrl = FileUtils.getLocalFilePath(orgUri);
		}else{
			//Uri uri = Uri.parse(BucketHelper.getInstance().getVideoBucketFullUrl(orgUri));
			playUrl = BucketHelper.getInstance().getVideoBucketFullUrl(orgUri);

//			HttpProxyCacheServer proxy = this.getCacheProxy();
//			playUrl = proxy.getProxyUrl(playUrl);
		}

		return playUrl;
	}

	//获取 视频文件缓存代理
	public HttpProxyCacheServer getCacheProxy() {
		if(null == cacheProxy){
		// 1 gb for cache, default 512M
//		cacheProxy = new HttpProxyCacheServer.Builder(this).maxCacheSize(1024 * 1024 * 1024).build();
		// limit total count of files in cache
//		cacheProxy = new HttpProxyCacheServer.Builder(this).maxCacheFilesCount(20).build();
			cacheProxy = new HttpProxyCacheServer(this);
		}
		return cacheProxy;
	}

}
