package com.intexh.bidong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.common.utils.StorageUtils;
import com.aliyun.struct.common.ScaleMode;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.baidu.platform.comapi.map.C;
import com.example.aliyunvideo.RecordVideoActivity;
import com.example.aliyunvideo.util.Common;
import com.intexh.bidong.constants.VideoInfo;
import com.intexh.bidong.utils.AppGlobalSetting;
import com.intexh.bidong.utils.FileUtils;
import com.intexh.bidong.utils.RecordResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by testuser on 16/3/15.
 */
public class QupaiHelper {
    private static AppGlobalSetting sp;

    //视频上传配置
    private final String AccessKeyId = "LTAIF9Uf8reQ4hho";
    private final String AccessKeySecret = "xxjDI5hpUf4Ey5lcNu0tP4XFxA1XKQ";
    private final String bucketName = "bdhlvip";
    private final String endpoint = "https://oss-cn-beijing.aliyuncs.com";

    //默认时长
    public static float DEFAULT_DURATION_LIMIT = 8;
    //默认最小时长
    public static float DEFAULT_MIN_DURATION_LIMIT = 2;
    //默认码率
    public static int DEFAULT_BITRATE = 2000 * 1000;

    public static int RECORDE_SHOW = 10001;

    private static final int RC_PERMISSION_PERM = 1;
    private static final int RC_SETTINGS_SCREEN = 1002;

    //水印本地路径，文件必须为rgba格式的PNG图片
    public static String WATER_MARK_PATH = "assets://Qupai/watermark/qupai-logo.png";
    private static String[] eff_dirs;

    public static void initQupai() {

        System.loadLibrary("openh264");
        System.loadLibrary("encoder");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
        QupaiHttpFinal.getInstance().initOkHttpFinal();

    }

    /**
     * 支持6.0动态权限
     */
//    private static boolean permissionCheck() {
//        String[] perms = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(PPStarApplication.applicationContext, perms)) {
//            return true;
//        } else {
//            // Ask for both permissions
//            EasyPermissions.requestPermissions(PPStarApplication.applicationContext, PPStarApplication.applicationContext.getResources().getString(R.string.permissions_tips_camera_audio),
//                    RC_PERMISSION_PERM, perms);
//            return false;
//        }
//    }
    private static void initAssetPath(Context context) {
        String path = StorageUtils.getCacheDirectory(context).getAbsolutePath() + File.separator + Common.QU_NAME + File.separator;
        eff_dirs = new String[]{
                null,
                path + "filter/chihuang",
                path + "filter/fentao",
                path + "filter/hailan",
                path + "filter/hongrun",
                path + "filter/huibai",
                path + "filter/jingdian",
                path + "filter/maicha",
                path + "filter/nonglie",
                path + "filter/rourou",
                path + "filter/shanyao",
                path + "filter/xianguo",
                path + "filter/xueli",
                path + "filter/yangguang",
                path + "filter/youya",
                path + "filter/zhaoyang"
        };
    }

    public static void showRecordActivity(final Activity activity, final int reqCode) {
        initAssetPath(activity);
        final AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                .setResolutionMode(AliyunSnapVideoParam.RESOLUTION_480P)    //分辨率480p 目前支持360p，480p，540p，720p
                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_3_4)  //视频比例 3:4 目前支持1:1,3:4,9:16
                .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO) ////设置录制模式，目前支持按录，点录和混合模式
                .setFilterList(eff_dirs)    //滤镜列表
                .setBeautyLevel(80) //美颜度
                .setBeautyStatus(true)  //设置美颜开关
                .setCameraType(CameraType.FRONT)  //设置前后置摄像头
                .setFlashType(FlashType.ON) // 设置闪光灯模式
                .setNeedClip(true)      //设置是否需要支持片段录制
                .setMaxDuration(8000)    //设置最大录制时长 单位毫秒
                .setMinDuration(2000)   //设置最小录制时长 单位毫秒
                .setVideoQuality(VideoQuality.HD) //视频质量
                .setGop(5)    ////设置关键帧间隔

                /**
                 * 裁剪参数
                 */
                .setMinVideoDuration(4000)
                .setMaxVideoDuration(29 * 1000)
                .setMinCropDuration(3000)
                .setFrameRate(25)
                .setCropMode(ScaleMode.PS)
                .setSortMode(AliyunSnapVideoParam.SORT_MODE_PHOTO)
                .build();
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Common.copyAll(activity);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                RecordVideoActivity.startRecordForResult(activity, reqCode, recordParam);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    //处理视频数据
    public static VideoInfo processVideoData(Intent data) {
        RecordResult result = new RecordResult(data);
        // 得到视频地址，和缩略图地址的数组，返回十张缩略图
        String videoPath = result.getPath();
        String[] thum = result.getThumbnail();
        String basePath = FileUtils.newOutgoingFilePath();
        String thumbPath = basePath + ".jpg";
        String newVideoPath = basePath + ".mp4";
        FileUtils.copyFile(videoPath, newVideoPath);
        FileUtils.copyFile(thum[0], thumbPath);
        VideoInfo localInfo = new VideoInfo();
        localInfo.thumbPath = thumbPath;
        localInfo.thumbFile = new File(thumbPath);
        localInfo.thumbName = localInfo.thumbFile.getName();
        localInfo.videoPath = newVideoPath;
        localInfo.videoFile = new File(newVideoPath);
        localInfo.videoName = localInfo.videoFile.getName();
//
//        /**
//         * 清除草稿,草稿文件将会删除。所以在这之前我们执行拷贝move操作。
//         * 上面的拷贝操作请自行实现，第一版本的copyVideoFile接口不再使用
//         */
////        qupaiService.deleteDraft(PPStarApplication.applicationContext, data);
//
        return localInfo;
    }

    //处理视频数据
    public static VideoInfo processVideoData(Context context,String videoPath) {
        VideoInfo localInfo = saveBitmip(context,getVideoThumbnail(videoPath));
        localInfo.videoPath = videoPath;
        localInfo.videoFile = new File(videoPath);
        localInfo.videoName = localInfo.videoFile.getName();
//
//        /**
//         * 清除草稿,草稿文件将会删除。所以在这之前我们执行拷贝move操作。
//         * 上面的拷贝操作请自行实现，第一版本的copyVideoFile接口不再使用
//         */
////        qupaiService.deleteDraft(PPStarApplication.applicationContext, data);
//
        return localInfo;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    public static VideoInfo saveBitmip(Context context,Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(getCacheDirectory(context,"image"), fileName);//设置文件名称
        if (file.exists()) {
            file.delete();
        }
        VideoInfo localInfo = new VideoInfo();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            localInfo.thumbPath = file.getAbsolutePath();
            localInfo.thumbFile = file;
            localInfo.thumbName =localInfo.thumbFile.getName();
            return localInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localInfo;
    }

    //文件上传
    public static void upLoad(){

    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     * @param context 上下文
     * @param type 文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public static File getCacheDirectory(Context context,String type) {
        File appCacheDir = getExternalCacheDirectory(context,type);
        if (appCacheDir == null){
            appCacheDir = getInternalCacheDirectory(context,type);
        }

//        if (appCacheDir == null){
//            Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !");
//        }else {
//            if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
//                Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !");
//            }
//        }
        return appCacheDir;
    }

    /**
     * 获取SD卡缓存目录
     * @param context 上下文
     * @param type 文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *             否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * {@link android.os.Environment#DIRECTORY_MUSIC},
     * {@link android.os.Environment#DIRECTORY_PODCASTS},
     * {@link android.os.Environment#DIRECTORY_RINGTONES},
     * {@link android.os.Environment#DIRECTORY_ALARMS},
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * {@link android.os.Environment#DIRECTORY_PICTURES}, or
     * {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public static File getExternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)){
                appCacheDir = context.getExternalCacheDir();
            }else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null){// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(),"Android/data/"+context.getPackageName()+"/cache/"+type);
            }

//            if (appCacheDir == null){
//                Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard unknown exception !");
//            }else {
//                if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
//                    Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !");
//                }
//            }
        }
//        else {
//            Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
//        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public static File getInternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)){
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        }else {
            appCacheDir = new File(context.getFilesDir(),type);// /data/data/app_package_name/files/type
        }

//        if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
//            Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !");
//        }
        return appCacheDir;
    }

}
