package com.intexh.bidong.trend;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadOptions;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager;

import java.io.File;
import java.util.ArrayList;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.constants.VideoInfo;
import com.intexh.bidong.location.LocationHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.me.CommitVideoRequest;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.FileUtils;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.utils.VolleyImageUtils;

/**
 * Created by wind on 16/3/9.
 */
public class UploadSimpleTask implements UploadListener {
    BaseActivity activity = null;
    int total = 0;
    int count = 0;
    String remark;
    ArrayList<String> photoPaths;
    ArrayList<String> outPhotoNames = new ArrayList<String>();
    VideoInfo videoInfo;

    public UploadSimpleTask(BaseActivity activity, String remark){
        this.activity = activity;
        this.remark = remark;
    }

    private ArrayList<String> compress(ArrayList<String> paths){
        ArrayList<String> outPaths = new ArrayList<>();
        for (String path : paths){
            String outPath = FileUtils.newOutgoingFilePath() + ".jpg";
            outPaths.add(outPath);
            VolleyImageUtils.compress(PPStarApplication.applicationContext, path, outPath);
        }
        return outPaths;
    }

    public void upload(ArrayList<String> paths){
        this.photoPaths = paths;
        total = paths.size();
        ArrayList<String> outPaths = compress(paths);
        for (String path : outPaths){
            File file = new File(path);
            outPhotoNames.add(file.getName());
            UploadOptions opt = new UploadOptions.Builder().aliases(file.getName()).build();
            PPStarApplication.wantuService.upload(file, opt, this, BucketHelper.getInstance().getVideoBucketToken());
        }
    }

    public void upload(VideoInfo videoInfo){
        this.videoInfo = videoInfo;
        total = 2;
        UploadOptions opt = new UploadOptions.Builder().aliases(videoInfo.videoName).build();
        PPStarApplication.wantuService.upload(videoInfo.videoFile, opt, this, BucketHelper.getInstance().getVideoBucketToken());
        opt = new UploadOptions.Builder().aliases(videoInfo.thumbName).build();
        PPStarApplication.wantuService.upload(videoInfo.thumbFile, opt, this, BucketHelper.getInstance().getVideoBucketToken());
    }

    @Override
    public void onUploading(UploadTask arg0) {}

    @Override
    public void onUploadFailed(UploadTask arg0, FailReason arg1) {
        if(null != activity){
            activity.showToast(R.string.error_uploadfile);
        }
    }

    @Override
    public void onUploadComplete(UploadTask arg0) {
        count++;
        if(total == count && null != videoInfo){
            doCommitVideo(videoInfo, remark);
        }

        if(total == count && null != photoPaths){
            doCommitPhotos(outPhotoNames, remark);
        }
    }

    @Override
    public void onUploadCancelled(UploadTask arg0) {
        if(null != activity){
            activity.showToast(R.string.error_uploadfile);
        }
    }

    private void doCommitVideo(VideoInfo videoInfo, String remark){
        CommitVideoRequest request = new CommitVideoRequest();
        request.setRemark(remark);
        request.setSnapshot(videoInfo.thumbName);
        request.setVideo(videoInfo.videoName);
        doCommit(request);
    }

    private void doCommitPhotos(ArrayList<String> photoNames, String remark){
        CommitVideoRequest request = new CommitVideoRequest();
        request.setRemark(remark);
        request.setPhotos(photoNames.toArray(new String[photoNames.size()]));
        doCommit(request);
    }

    private void doCommit(CommitVideoRequest request){
        request.setUserid(UserUtils.getUserid());
        LocationHelper.LocationInfo locObj = LocationLocalManager.getInstance().getLastLocation();
        if(locObj != null && locObj.location != null){
            request.setLoc(locObj.location.longitude + "," + locObj.location.latitude);
            request.setCity(locObj.city);
            request.setDistrict(locObj.district);
            request.setStreet(locObj.street);
        }

        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<String>() {
            @Override
            public void onSuccess(String data) {}

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                if(null != activity) {
                    activity.showToast(reason);
                }
            }
        });
        request.getDataFromServer();
    }
}