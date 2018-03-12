package com.intexh.bidong.gift;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import com.intexh.bidong.R;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.utils.ImageUtils;

/**
 * Created by Administrator on 2018/3/12.
 */

public class GiftSendDialog extends AlertDialog {

    View background;
    ImageView ivGift;
    GiftItemEntity entity;

    public GiftSendDialog(@NonNull Context context, GiftItemEntity entity) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.entity = entity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gift_send);
        background = findViewById(R.id.background);
        ivGift = (ImageView) findViewById(R.id.ivGiftPanel);
        ImageUtils.loadGiftImage(entity.getUrl(), ivGift);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    @Override
    public void show() {
        super.show();
        showPanel(true);
        if (background == null) return;
        background.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        },  1000);  // 3秒之后
    }

    @Override
    public void onBackPressed() {
        showPanel(false);
        background.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 200);
    }

    private void showPanel(boolean show) {
        if (show) {
            ivGift.animate().scaleX(6f).scaleY(6f).setDuration(500).start();
            background.animate().alpha(1).setDuration(200).start();
        } else {
            ivGift.animate().scaleY(1f).scaleX(1f).setDuration(500).start();
            background.animate().alpha(0).setDuration(200).start();
        }
    }
}
