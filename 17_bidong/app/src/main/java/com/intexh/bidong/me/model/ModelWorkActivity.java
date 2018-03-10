package com.intexh.bidong.me.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.baoyz.actionsheet.ActionSheet;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager;
import com.manteng.common.GsonUtils;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.me.CommitModelWorksRequest;
import com.intexh.bidong.userentity.ModelWorkItemEntity;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.FileUtils;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.RealHeightRecylerView;
import com.intexh.bidong.widgets.SpacesItemDecoration;

/**
 * Created by shenxin on 15/12/28.
 */
public class ModelWorkActivity extends BaseTitleActivity implements View.OnClickListener{
    public static final String MODEL_WORK = "MODEL_WORK";

    @ViewInject(R.id.recyler_modelwork_main)
    private RealHeightRecylerView mainView;
    @ViewInject(R.id.txt_title_confirm)
    private TextView confirmView;
    @ViewInject(R.id.edit_modelwork_title)
    private EditText titleInput;
    private ModelPicsAdapter adapter = null;
    private List<ModelWorkItemEntity> datas = new ArrayList<ModelWorkItemEntity>();
    private static final int REQUEST_IMAGES = 100;
    private Uri tempUri = null;
    public static final int CAPTURE_PHOTO_REQUEST_CODE = REQUEST_IMAGES + 1;
    public static final int REQUEST_CROPIMAGE = CAPTURE_PHOTO_REQUEST_CODE + 1;
    public static final int REQUEST_LOCAL_PIC = REQUEST_CROPIMAGE + 1;
    private int uploadIndex = 1;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_modelwork);
        confirmView.setVisibility(View.VISIBLE);
        confirmView.setText("上传");
        setTitleText("上传作品");
        confirmView.setOnClickListener(this);
        ModelWorkItemEntity entity = new ModelWorkItemEntity();
        entity.setUri("drawable://" + R.drawable.plus);
        datas.add(entity);
        adapter = new ModelPicsAdapter();
        adapter.setIsShowldShowHeader(false);
        adapter.setDatas(datas);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        SpacesItemDecoration spaceManager = new SpacesItemDecoration(spacingInPixels, 3);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        mainView.setLayoutManager(manager);
        mainView.addItemDecoration(spaceManager);
        mainView.setAdapter(adapter);
        mainView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(0 == position){
                    jumpToPic();
                }else{
                    List<ModelWorkItemEntity> array = new ArrayList<ModelWorkItemEntity>();
                    for(int i=1;i<datas.size();i++){
                        array.add(datas.get(i));
                    }
                    Intent intent = new Intent(ModelWorkActivity.this, ModelImageBrowserActivity.class);
                    intent.putExtra(ModelImageBrowserActivity.IMAGE_ENTIES, GsonUtils.objToJson(array));
                    intent.putExtra(ModelImageBrowserActivity.CUR_INDEX, position-1);
                    startActivityForResult(intent, REQUEST_IMAGES);
                }
            }
        }));
    }

    private void jumpToPic(){
        if(datas.size() > 6){
            showToast("最多只能添加6张照片");
            return ;
        }
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(R.string.common_cancel)
                .setOtherButtonTitles(
                        getResources()
                                .getString(R.string.common_takecamera),
                        getResources().getString(R.string.common_photo))
                .setListener(new ActionSheet.ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet,
                                                   int index) {
                        switch (index) {
                            case 0: {
                                Intent captureIntent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // CameraCrackUtils.crackCamera(getActivity(),
                                // captureIntent);
                                File path = Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                File outputImage = new File(path, FileUtils.genPicFileName()
                                        + ".jpg");
                                tempUri = Uri.fromFile(outputImage);
                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        tempUri);
                                startActivityForResult(captureIntent,
                                        CAPTURE_PHOTO_REQUEST_CODE);
                                break;
                            }
                            case 1: {
                                Intent intent = new Intent(
                                        Intent.ACTION_GET_CONTENT, null);
                                File path = Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                File outputImage = new File(path, FileUtils.genPicFileName()
                                        + ".jpg");
                                tempUri = Uri.fromFile(outputImage);
                                intent.setType("image/*");
                                // 设置在开启的Intent中设置显示的view可裁剪
                                // 这段代码里设置成false也能裁剪啊。。。这是为什么？懂的给我讲讲了
                                // 这段注释掉就不会跳转到裁剪的activity
                                intent.putExtra("crop", "true");
                                // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图
                                intent.putExtra("aspectX", 1);
                                intent.putExtra("aspectY", 1);
                                // 裁剪区的宽和高 其实就是裁剪后的显示区域
                                // 若裁剪的比例不是显示的比例，则自动压缩图片填满显示区域。若设置为0,0
                                // 就不显示。若不设置，则按原始大小显示
                                // 不知道有啥用。。可能会保存一个比例值 需要相关文档啊
                                intent.putExtra("scale", true);
                                // true的话直接返回bitmap，可能会很占内存 不建议
                                intent.putExtra("return-data", false);
                                // 上面设为false的时候将MediaStore.EXTRA_OUTPUT即"output"关联一个Uri
                                intent.putExtra("output", tempUri);
                                // 看参数即可知道是输出格式
                                intent.putExtra("outputFormat",
                                        Bitmap.CompressFormat.JPEG.toString());
                                // 面部识别 这里用不上
                                intent.putExtra("noFaceDetection", false);
                                // 想从Activity中获得返回数据，在启动Activity时候使用startActivityForResult方法
                                // 1为请求代码，可以是任意值，个人感觉用资源id会比较清楚，而且不会重复
                                // 比如当前控件的R.id.button
                                startActivityForResult(intent,
                                        REQUEST_LOCAL_PIC);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet,
                                          boolean isCancel) {

                    }
                }).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.txt_title_confirm:{
                uploadPic();
                break;
            }
        }
    }

    private void uploadPic(){
        String ss = titleInput.getText().toString();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入作品图片说明");
            return ;
        }
        if(datas.size() == 1){
            showToast("请选择作品图片");
            return ;
        }
        uploadIndex = 1;
        uploadPicsImpl();
    }

    private void doCommitWork(){
        CommitModelWorksRequest request = new CommitModelWorksRequest();
        request.setUserid(UserUtils.getUserid());
        request.setTitle(titleInput.getText().toString());
        String ss = "";
        for(int i=1;i<datas.size();i++){
            ModelWorkItemEntity entity = datas.get(i);
            ss += entity.getRemoteName() + ",";
        }
        ss = ss.substring(0,ss.length()-1);
        request.setUri(ss);
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<String[]>() {
            @Override
            public void onSuccess(String[] data) {
                hideLoading();
                List<ModelWorkItemEntity> array = new ArrayList<ModelWorkItemEntity>();
                for(int i=1;i<datas.size();i++){
                    String id = data[i-1];
                    ModelWorkItemEntity entity = datas.get(i);
                    entity.setId(id);
                    entity.setTitle(titleInput.getText().toString());
                    array.add(entity);
                }
                Intent intent = new Intent();
                intent.putExtra(MODEL_WORK,GsonUtils.objToJson(array));
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                hideLoading();
                showToast(reason);
            }
        });
        showLoading();
        request.getDataFromServer();
    }

    private void uploadPicsImpl() {
        if(uploadIndex == datas.size()){
            doCommitWork();
            return ;
        }
        ModelWorkItemEntity entity = datas.get(uploadIndex);
        if(null != entity.getRemoteName()){
            uploadIndex++;
        }else{
            showLoading();
            PPStarApplication.wantuService.upload(entity.getFile(), null, new UploadListener() {

                @Override
                public void onUploading(UploadTask arg0) {}

                @Override
                public void onUploadFailed(UploadTask arg0, FailReason arg1) {
                    hideLoading();
                    showToast("图片上传失败");
                }

                @Override
                public void onUploadComplete(UploadTask arg0) {
                    hideLoading();
                    ModelWorkItemEntity itemEntity = datas.get(uploadIndex);
                    itemEntity.setRemoteName(arg0.getResult().name);
                    uploadIndex++;
                    uploadPicsImpl();
                }

                @Override
                public void onUploadCancelled(UploadTask arg0) {
                    hideLoading();
                    showToast("图片上传失败");
                }
            }, BucketHelper.getInstance().getWorksBucketToken());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOCAL_PIC: {
                    String picturePath = tempUri.getPath();// cursor.getString(columnIndex);
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    String ss = getExternalCacheDir()
                            .getAbsolutePath()
                            + File.separator
                            + FileUtils.genPicFileName();
                    if(null == bitmap){
                        Uri uri = data.getData();
                        picturePath = ImageUtils.getPath(this,uri);
                        try {
                            bitmap = ImageUtils.resampleImageEx(picturePath, 400);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ImageUtils.saveBitmapToDisk(bitmap, ss, this);
                    if(null != tempUri){
                        FileUtils.deleteFile(tempUri.getPath());
                    }
                    ModelWorkItemEntity entity = new ModelWorkItemEntity();
                    entity.setUri("file:///" + ss);
                    entity.setFile(new File(ss));
                    entity.setName(FileUtils.genPicFileName());
                    datas.add(entity);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case CAPTURE_PHOTO_REQUEST_CODE: {
                    // 裁剪图片意图
                    File path = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File outputImage = new File(path, "avatar1"
                            + ".jpg");
                    Uri outtempUri = Uri.fromFile(outputImage);
                    Crop.of(tempUri, outtempUri).asSquare().start(this);
                    tempUri = outtempUri;
//                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(tempUri, "image/*");
//                    intent.putExtra("crop", "true");
//                    // 裁剪框的比例，1：1
//                    intent.putExtra("aspectX", 1);
//                    intent.putExtra("aspectY", 1);
//                    // 图片格式
//                    intent.putExtra("outputFormat", "JPEG");
//                    intent.putExtra("noFaceDetection", true);
//                    intent.putExtra("return-data", true);
//                    File path = Environment
//                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                    File outputImage = new File(path, "avatar1"
//                            + ".jpg");
//                    tempUri = Uri.fromFile(outputImage);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//                    startActivityForResult(intent, REQUEST_CROPIMAGE);
                    break;
                }
                case Crop.REQUEST_CROP: {
                    Bitmap bitmap = null;
                    Uri photoUri = tempUri;//data.getData();
                    if (photoUri != null) {
                        bitmap = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (bitmap == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            bitmap = (Bitmap) extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        }
                    }
                    String ss = getExternalCacheDir()
                            .getAbsolutePath()
                            + File.separator
                            + FileUtils.genPicFileName();
                    ImageUtils.saveBitmapToDisk(bitmap, ss, this);
                    if(null != tempUri){
                        FileUtils.deleteFile(tempUri.getPath());
                    }
                    ModelWorkItemEntity entity = new ModelWorkItemEntity();
                    entity.setUri("file:///" + ss);
                    entity.setFile(new File(ss));
                    entity.setName(FileUtils.genPicFileName());
                    datas.add(entity);
                    break;
                }
                case REQUEST_IMAGES:{
                    ModelWorkItemEntity entity = datas.get(0);
                    datas.clear();
                    datas.add(entity);
                    String ss = data.getStringExtra(ModelImageBrowserActivity.IMAGE_ENTIES);
                    ModelWorkItemEntity[] items = GsonUtils.jsonToObj(ss,ModelWorkItemEntity[].class);
                    if(null != items){
                        for(ModelWorkItemEntity item : items){
                            datas.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
