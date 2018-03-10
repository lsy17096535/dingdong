package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.userentity.WatchItemEntity;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by shenxin on 15/12/30.
 */
public class VisitMeActivity extends BaseTitleActivity {

    @ViewInject(R.id.recycler_watchme_main)
    private RecyclerView mainView;
    @ViewInject(R.id.pull_watchme_pull)
    private PtrClassicFrameLayout pullLayout;
    @ViewInject(R.id.layout_watchme_empty)
    private LinearLayout emptylayout;
    @ViewInject(R.id.txt_watchme_empty_tip)
    private TextView emptyTipView;
    @ViewInject(R.id.txt_watchme_empty_action)
    private TextView emptyActionView;

    private VisitMeAdapter adapter = null;
    private List<WatchItemEntity> datas = new ArrayList<WatchItemEntity>();
    private Page page = new Page();
    private boolean isNeedClear = false;
    private boolean isNoMoreData = false;

    private View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(VisitMeActivity.this, MyTrendVideoActivity.class);
            intent.putExtra(MyTrendVideoActivity.IS_SHOULDRECORD, true);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_watchme);
        setTitleText("最近来访");
        pullLayout.setPullToRefresh(true);
        pullLayout.setMode(PtrFrameLayout.Mode.BOTH);
        pullLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                if (isNoMoreData) {
                    pullLayout.refreshComplete();
                } else {
                    getDatas();
                }
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                isNeedClear = true;
                getDatas();
            }
        });
        adapter = new VisitMeAdapter();
        adapter.setDatas(datas);
        GridLayoutManager layoutManager = new GridLayoutManager(this,4);
        mainView.setLayoutManager(layoutManager);
        mainView.setAdapter(adapter);
        mainView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WatchItemEntity entity = datas.get(position);
                getUserDetail(entity);
            }
        }));
        getDatas();
    }

    private void getDatas(){
        GetVisitMeRequest request = new GetVisitMeRequest();
        request.setUserid(UserUtils.getUserid());
        if(isNeedClear){
            request.setPage(new Page());
        }else{
            request.setPage(page);
        }
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<WatchItemEntity[]>() {
            @Override
            public void onSuccess(WatchItemEntity[] data) {
                hideLoading();
                pullLayout.refreshComplete();
                if (isNeedClear) {
                    isNeedClear = false;
                    datas.clear();
                    page.setStart(0);
                }
                if (null != data) {
                    for (WatchItemEntity entity : data) {
                        datas.add(entity);
                    }
                    if (data.length < page.getCount()) {
                        isNoMoreData = true;
                    }
                    page.setStart(data.length);
                } else {
                    isNoMoreData = true;
                }
                adapter.notifyDataSetChanged();
                updateEmptyView();
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                hideLoading();
                isNeedClear = true;
                pullLayout.refreshComplete();
                showToast(reason);
            }
        });
        showLoading();
        request.getDataFromServer();
    }

    private void updateEmptyView(){
        if(!datas.isEmpty()){
            pullLayout.setVisibility(View.VISIBLE);
            emptylayout.setVisibility(View.GONE);
        }else{
            pullLayout.setVisibility(View.GONE);
            emptylayout.setVisibility(View.VISIBLE);
            emptylayout.setOnClickListener(recordListener);
            emptyActionView.setVisibility(View.GONE);
        }
    }

    private void getUserDetail(final WatchItemEntity entity){
        GetUserDetailRequest request = new GetUserDetailRequest();
        request.setUserid(entity.getUser_id());
        request.setGender(entity.getGender());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<User>() {
            @Override
            public void onSuccess(User data) {
                hideLoading();
                data.setVideo(entity.getVideo());
                Intent intent = new Intent(VisitMeActivity.this, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(data));
                startActivity(intent);
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
}
