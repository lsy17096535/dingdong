package com.intexh.bidong.net;

import com.lzy.okgo.OkGo;

/**
 * Created by FrankZhang on 2017/11/14.
 */

public enum OkHelper {
    INSTANCE;

    public void cancelTag(Object o) {
        OkGo.getInstance().cancelTag(o); //取消请求
    }

}
