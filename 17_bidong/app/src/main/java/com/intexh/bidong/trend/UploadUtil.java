package com.intexh.bidong.trend;

import java.util.ArrayList;

import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.constants.VideoInfo;

/**
 * Created by wind on 16/3/8.
 */
public class UploadUtil {

    public static void uploadVideo(final BaseActivity activity, final String remark, final VideoInfo videoInfo) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                UploadSimpleTask task = new UploadSimpleTask(activity, remark);
                task.upload(videoInfo);
            }
        };
        thread.start();
    }

    public static void uploadPhotos(final BaseActivity activity, final String remark, final ArrayList<String> paths) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                UploadSimpleTask task = new UploadSimpleTask(activity, remark);
                task.upload(paths);
            }
        };
        thread.start();
    }
}
