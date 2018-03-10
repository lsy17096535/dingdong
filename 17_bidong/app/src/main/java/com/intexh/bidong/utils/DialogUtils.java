package com.intexh.bidong.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by FrankZhang on 2017/11/6.
 */

public class DialogUtils {

    public static void showDialog(final Activity activity, String title, String message, String okBtn, String cancelBtn, final DialogImpl impl){
        new AlertDialog.Builder(activity).setTitle(title)
                .setMessage(message)
                .setPositiveButton(okBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        impl.onOk();
                    }
                })
                .setNeutralButton(cancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        impl.onCancel();
                    }
                })
                .create()
                .show();
    }

    public static class DialogImpl{
       public void onOk(){}
        public void onCancel(){}
    }
}
