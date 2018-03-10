package com.intexh.bidong.me.model;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.me.DeleteModelWorksPicRequest;
import com.intexh.bidong.userentity.ModelWorkItemEntity;
import com.intexh.bidong.utils.ImageUtils;

/**
 * Created by shenxin on 15/12/28.
 */
public class ModelImageBrowserActivity extends BaseActivity implements View.OnClickListener{
    public static final String IMAGE_ENTIES = "IMAGE_ENTIES";
    public static final String CUR_INDEX = "CUR_INDEX";

    @ViewInject(R.id.image_imagebrowser_back)
    private ImageView backView;
    @ViewInject(R.id.txt_imagebrowser_title)
    private TextView titleView;
    @ViewInject(R.id.image_imagebrowser_del)
    private ImageView delView;
    @ViewInject(R.id.pager_imagebrowser_main)
    private ViewPager pager;
    @ViewInject(R.id.txt_imagebrowser_content)
    private TextView contentView;
    @ViewInject(R.id.layout_imagebrowser_main)
    private View mainView;
    @ViewInject(R.id.layout_imagebrowser_title)
    private View titleLayout;
    private List<ModelWorkItemEntity> datas = new ArrayList<ModelWorkItemEntity>();
    private ImagePagerAdapter adapter = null;
    private boolean isShow = true;
    private int index = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_imagebrowser);
        ViewUtils.inject(this);
        mainView.setOnClickListener(this);
        backView.setOnClickListener(this);
        delView.setOnClickListener(this);
        index = getIntent().getIntExtra(CUR_INDEX,0);
        String ss = getIntent().getStringExtra(IMAGE_ENTIES);
        ModelWorkItemEntity[] items = GsonUtils.jsonToObj(ss,ModelWorkItemEntity[].class);
        if(null != items){
            for(ModelWorkItemEntity entity : items){
                datas.add(entity);
            }
        }
        adapter = new ImagePagerAdapter();
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                titleView.setText(String.valueOf(position + 1) + "/" + String.valueOf(datas.size()));
                ModelWorkItemEntity entity = datas.get(position);
                contentView.setText(entity.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateTitle();
        pager.setCurrentItem(index);
        titleView.setText(String.valueOf(index + 1) + "/" + String.valueOf(datas.size()));
        ModelWorkItemEntity entity = datas.get(index);
        contentView.setText(entity.getTitle());
    }

    private void updateTitle(){
        if(isShow){
            titleLayout.setVisibility(View.VISIBLE);
        }else{
            titleLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.image_imagebrowser_back:{
                finish();
                break;
            }
            case R.id.image_imagebrowser_del:{
                ModelWorkItemEntity entity = datas.get(index);
                if(null != entity.getId()){
                    DeleteModelWorksPicRequest request = new DeleteModelWorksPicRequest();
                    request.setWorkPicId(entity.getId());
                    request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            hideLoading();
                            showToast("删除成功");
                            datas.remove(index);
                            Intent intent = new Intent();
                            intent.putExtra(IMAGE_ENTIES, GsonUtils.objToJson(datas));
                            setResult(RESULT_OK, intent);
                            adapter = new ImagePagerAdapter();
                            pager.setAdapter(adapter);
                            if(0 == datas.size()){
                                finish();
                            }else{
                                if(index < datas.size()){
                                    pager.setCurrentItem(index);
                                }else{
                                    index = 0;
                                    pager.setCurrentItem(0);
                                }
                                titleView.setText(String.valueOf(index + 1) + "/" + String.valueOf(datas.size()));
                                ModelWorkItemEntity entity = datas.get(index);
                                contentView.setText(entity.getTitle());
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
                } else{
                    showToast("删除成功");
                    datas.remove(index);
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_ENTIES,GsonUtils.objToJson(datas));
                    setResult(RESULT_OK, intent);
                    adapter = new ImagePagerAdapter();
                    pager.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                    if(0 == datas.size()){
                        finish();
                    }else{
                        if(index < datas.size()){
                            pager.setCurrentItem(index);
                        }else{
                            index = 0;
                            pager.setCurrentItem(0);
                        }
                        titleView.setText(String.valueOf(index + 1) + "/" + String.valueOf(datas.size()));
                        ModelWorkItemEntity item = datas.get(index);
                        contentView.setText(item.getTitle());
                    }
                }
                break;
            }
            case R.id.layout_imagebrowser_main:{
                isShow = !isShow;
                updateTitle();
            }
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isShow = !isShow;
            updateTitle();
        }
    };

    private class ImagePagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView photoView = new ImageView(container.getContext());
//            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//                @Override
//                public void onViewTap(View view, float x, float y) {
//                    isShow = !isShow;
//                    updateTitle();
//                }
//            });
            photoView.setOnClickListener(mListener);
            ModelWorkItemEntity entity = datas.get(position);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageUtils.loadWorksImage(entity.getUri(), photoView, 1.0f);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
