package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.common.CareerPickerActivity;
import com.intexh.bidong.common.CommonInputActivity;
import com.intexh.bidong.me.model.ModelImageBrowserActivity;
import com.intexh.bidong.me.model.ModelPicsAdapter;
import com.intexh.bidong.me.model.ModelWorkActivity;
import com.intexh.bidong.userentity.ModelInfoEntity;
import com.intexh.bidong.userentity.ModelWorkItemEntity;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.RealHeightRecylerView;
import com.intexh.bidong.widgets.SpacesItemDecoration;

/**
 * Created by shenxin on 15/12/28.
 */
public class ModelInfoActivity extends BaseTitleActivity implements View.OnClickListener{
    @ViewInject(R.id.txt_title_confirm)
    private TextView confirmTxt;
//    @ViewInject(R.id.edit_modelinfo_country)
//    private EditText countryInput;
//    @ViewInject(R.id.edit_modelinfo_shoesize)
//    private EditText shoeSizeInput;
//    @ViewInject(R.id.edit_modelinfo_tunwei)
//    private EditText tunweiInput;
//    @ViewInject(R.id.edit_modelinfo_xiongwei)
//    private EditText xiongweiInput;
//    @ViewInject(R.id.edit_modelinfo_yaowei)
//    private EditText yaoweiInput;
//    @ViewInject(R.id.txt_modelinfo_cupsize)
//    private TextView cupsizeTxt;
//    @ViewInject(R.id.txt_modelinfo_serviceinfo)
//    private TextView serviceInfoTxt;
//    @ViewInject(R.id.layout_modelinfo_cupsize)
//    private View cupsizeLayout;
//    @ViewInject(R.id.layout_modelinfo_serviceinfo)
//    private View serviceInfoLayout;
    @ViewInject(R.id.recyle_modelinfo_pics)
    private RealHeightRecylerView picsView;
    private ModelPicsAdapter adapter = null;
    private List<ModelWorkItemEntity> datas = new ArrayList<ModelWorkItemEntity>();
    private ModelInfoEntity modelInfoEntity = null;
    private static final int REQUEST_CUPS = 100;
    private static final int REQUEST_SERVICEINFO = 101;
    private static final int REQUEST_IMAGES = 102;
    private static final int REQUEST_PUBLISH = 103;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_modelinfo);
        setTitleText("演艺资料");
        confirmTxt.setText("保存");
        confirmTxt.setVisibility(View.VISIBLE);
        confirmTxt.setOnClickListener(this);
//        worksView.setOnClickListener(this);
//        serviceInfoLayout.setOnClickListener(this);
//        if(1 == UserUtils.getUserInfo().getUser().getGender()){
//            cupsizeLayout.setVisibility(View.VISIBLE);
//        }else{
//            cupsizeLayout.setVisibility(View.GONE);
//        }
//        cupsizeLayout.setOnClickListener(this);
        getModelInfo();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        SpacesItemDecoration spaceManager = new SpacesItemDecoration(spacingInPixels, 3);
        spaceManager.setStartOffset(1);
        GridLayoutManager manager = new GridLayoutManager(this,3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(0 == position){
                    return 3;
                }
                return 1;
            }
        });
        picsView.setLayoutManager(manager);
        adapter = new ModelPicsAdapter();
        adapter.setAdapterListener(new ModelPicsAdapter.OnModelViewClickListener() {
            @Override
            public void onClickCupSize() {
                Intent intent = new Intent(ModelInfoActivity.this, CareerPickerActivity.class);
                intent.putExtra(CareerPickerActivity.MODE,1);
                intent.putExtra(CareerPickerActivity.CAREER,adapter.getCupSize());
                startActivityForResult(intent, REQUEST_CUPS);
            }

            @Override
            public void onClickServiceInfo() {
                Intent intent = new Intent(ModelInfoActivity.this, CommonInputActivity.class);
                intent.putExtra(CommonInputActivity.MAX_LENGTH,256);
                intent.putExtra(CommonInputActivity.TIPS,"");
                intent.putExtra(CommonInputActivity.TITLE,"服务范围/价格");
                intent.putExtra(CommonInputActivity.VALUE,adapter.getServiceInfo());
                startActivityForResult(intent,REQUEST_SERVICEINFO);
            }

            @Override
            public void onClickWorks() {
                Intent intent = new Intent(ModelInfoActivity.this, ModelWorkActivity.class);
                startActivityForResult(intent,REQUEST_PUBLISH);
            }
        });
        adapter.setDatas(datas);
        picsView.setAdapter(adapter);
        picsView.addItemDecoration(spaceManager);
        picsView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {

                } else {
                    Intent intent = new Intent(ModelInfoActivity.this, ModelImageBrowserActivity.class);
                    intent.putExtra(ModelImageBrowserActivity.IMAGE_ENTIES, GsonUtils.objToJson(datas));
                    intent.putExtra(ModelImageBrowserActivity.CUR_INDEX, position - 1);
                    startActivityForResult(intent, REQUEST_IMAGES);
                }
            }
        }));
        updateViews();
    }

    private void updateViews(){
        if(null == modelInfoEntity){
            adapter.setXiongwei("");
            adapter.setYaowei("");
            adapter.setTunwei("");
            adapter.setShoesize("");
            adapter.setCupSize("");
            adapter.setCountry("");
            adapter.setServiceInfo("");
        }else{
            adapter.setXiongwei(modelInfoEntity.getBust() + "");
            adapter.setYaowei(modelInfoEntity.getWaist() + "");
            adapter.setTunwei(modelInfoEntity.getHips() + "");
            adapter.setShoesize(modelInfoEntity.getShoes() + "");
            adapter.setCupSize(modelInfoEntity.getCups());
            adapter.setCountry(modelInfoEntity.getCountry());
            adapter.setServiceInfo(modelInfoEntity.getOffer());
        }
        adapter.notifyDataSetChanged();
    }

    private void getModelInfo(){
        GetModelInfoRequest request = new GetModelInfoRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<ModelInfoEntity>() {
            @Override
            public void onSuccess(ModelInfoEntity data) {
                hideLoading();
                modelInfoEntity = data;
                updateViews();
//                cupsizeLayout.setEnabled(true);
//                serviceInfoLayout.setEnabled(true);
                confirmTxt.setEnabled(true);
//                worksView.setEnabled(true);
                if(null != data){
                    getModelWorks();
                }
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

    private void getModelWorks(){
        GetModelWorksRequest request = new GetModelWorksRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<ModelWorkItemEntity[]>() {
            @Override
            public void onSuccess(ModelWorkItemEntity[] data) {
                hideLoading();
                datas.clear();
                if(null != data){
                    for(ModelWorkItemEntity entity : data){
                        datas.add(entity);
                    }
                }
                adapter.notifyDataSetChanged();
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

    private void commitModelInfo(){
        UpdateModelInfoRequest request = new UpdateModelInfoRequest();
        request.setUserid(UserUtils.getUserid());
        String ss = adapter.getXiongwei();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入胸围");
            return ;
        }
        int size = 0;
        if(RegexUtil.checkDigit(ss)){
            size = Integer.parseInt(ss);
            if(size < 40 || size > 120){
                showToast("请输入40-120cm的胸围");
                return ;
            }
            request.setBust(size);
        }else{
            showToast("请输入40-120cm的胸围");
            return ;
        }
        ss = adapter.getYaowei();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入腰围");
            return ;
        }
        size = 0;
        if(RegexUtil.checkDigit(ss)){
            size = Integer.parseInt(ss);
            if(size < 50 || size > 150){
                showToast("请输入50-150cm的腰围");
                return ;
            }
            request.setWaist(size);
        }else{
            showToast("请输入50-150cm的腰围");
            return ;
        }

        ss = adapter.getTunwei();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入臀围");
            return ;
        }
        size = 0;
        if(RegexUtil.checkDigit(ss)){
            size = Integer.parseInt(ss);
            if(size < 50 || size > 120){
                showToast("请输入50-120cm的臀围");
                return ;
            }
            request.setHips(size);
        }else{
            showToast("请输入50-120cm的臀围");
            return ;
        }

        ss = adapter.getShoesize();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入鞋码");
            return ;
        }
        size = 0;
        if(RegexUtil.checkDigit(ss)){
            size = Integer.parseInt(ss);
            if(size < 13 || size > 60){
                showToast("请输入13-60cm的鞋码");
                return ;
            }
            request.setShoeSize(size);
        }else{
            showToast("请输入13-60cm的鞋码");
            return ;
        }
        if(1 == UserUtils.getUserInfo().getUser().getGender()){
            ss = adapter.getCupSize();
            if(StringUtil.isEmptyString(ss)){
                showToast("请选择罩杯");
                return ;
            }
            request.setCups(ss);
        }
        ss = adapter.getCountry();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入国家");
            return ;
        }
        request.setCountry(ss);
        ss = adapter.getServiceInfo();
        if(StringUtil.isEmptyString(ss)){
            showToast("请输入服务范围/报价");
            return ;
        }
        request.setOffer(ss);
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<String>() {
            @Override
            public void onSuccess(String data) {
                hideLoading();
                showToast("信息更新成功");
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


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.txt_title_confirm:{
                commitModelInfo();
                break;
            }
            case R.id.layout_modelinfo_cupsize:{
                Intent intent = new Intent(this, CareerPickerActivity.class);
                intent.putExtra(CareerPickerActivity.MODE,1);
                intent.putExtra(CareerPickerActivity.CAREER,adapter.getCupSize());
                startActivityForResult(intent, REQUEST_CUPS);
                break;
            }
            case R.id.layout_modelinfo_serviceinfo:{
                Intent intent = new Intent(this, CommonInputActivity.class);
                intent.putExtra(CommonInputActivity.MAX_LENGTH,256);
                intent.putExtra(CommonInputActivity.TIPS,"");
                intent.putExtra(CommonInputActivity.TITLE,"服务范围/价格");
                intent.putExtra(CommonInputActivity.VALUE,adapter.getServiceInfo());
                startActivityForResult(intent,REQUEST_SERVICEINFO);
                break;
            }
            case R.id.layout_modelinfo_works:{
                Intent intent = new Intent(this, ModelWorkActivity.class);
                startActivityForResult(intent,REQUEST_PUBLISH);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            switch(requestCode){
                case REQUEST_CUPS:{
                    String cup = data.getStringExtra(CareerPickerActivity.CAREER);
                    adapter.setCupSize(cup);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case REQUEST_SERVICEINFO:{
                    String ss = data.getStringExtra(CommonInputActivity.VALUE);
                    adapter.setServiceInfo(ss);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case REQUEST_PUBLISH:{
                    String ss = data.getStringExtra(ModelWorkActivity.MODEL_WORK);
                    ModelWorkItemEntity[] items = GsonUtils.jsonToObj(ss,ModelWorkItemEntity[].class);
                    if(null != items){
                        for(ModelWorkItemEntity entity : items){
                            datas.add(0,entity);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
                case REQUEST_IMAGES:{
                    String ss = data.getStringExtra(ModelImageBrowserActivity.IMAGE_ENTIES);
                    ModelWorkItemEntity[] items = GsonUtils.jsonToObj(ss,ModelWorkItemEntity[].class);
                    datas.clear();
                    if(null != items){
                        for(ModelWorkItemEntity entity : items){
                            datas.add(0,entity);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
