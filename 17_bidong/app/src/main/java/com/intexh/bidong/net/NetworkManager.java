package com.intexh.bidong.net;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by intexh on 2016/6/4.
 */
public enum NetworkManager {
    INSTANCE;

    public void init(Application application) {
        //必须调用初始化
        OkGo.getInstance().init(application);
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //全局的读取超时时间
//        builder.readTimeout(15000, TimeUnit.MILLISECONDS);
//        //全局的写入超时时间
//        builder.writeTimeout(15000, TimeUnit.MILLISECONDS);
//        //全局的连接超时时间
//        builder.connectTimeout(15000, TimeUnit.MILLISECONDS);
    }

    public void post(final String api, final Map<String, String> params, final OnRequestCallBack callBack) {
        post("",api,params,callBack);
    }
    /**
     * @param api      请求接口
     * @param callBack
     */
    public void post(Object tag, final String api, final Map<String, String> params, final OnRequestCallBack callBack) {
        OkGo.<String>post(api)//
                .tag(tag)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        success(response.body(), api, callBack, params);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        error(response.getException().toString(), api, params, callBack);
                    }

                });

    }

    private void error(String err, String api, Map<String, String> params, OnRequestCallBack callBack) {
        if(err.contains("Unable to resolve host")){
            callBack.onError("网络连接失败，请检查您的网络");
        }else if(err.contains("Failed to connect")||err.contains("failed to connect")){
            callBack.onError("服务器连接失败，请稍后再试或联系客服");
        }else if(err.contains("timed out")||err.contains("timeout")){
            callBack.onError("请求超时,请重试");
        }else{
            callBack.onError(err);
        }
        Log.e("okhttp:", "*---------------------------------------------------------------------------*");
        Log.e("okhttp:", "requestUrl= "+ api);
        Log.e("okhttp:", "isSuccess = 请求错误" + err);
        Log.e("okhttp:", "requestParams： " + GsonUtils.serializedToJson(params));
        Log.e("okhttp:", "response= " + err);
        Log.e("okhttp:", "*                                                                      *");

    }

    private void success(String s, String api, OnRequestCallBack callBack, Map<String, String> params) {
        Log.e("okhttp:", "*---------------------------------------------------------------------------*");
        Log.e("okhttp:", "requestUrl= " + api);
        if (s == null) {
            Log.e("okhttp:", "isSuccess = true 数据返回失败");
            print(s,params);
            return;
        }
        print(s, params);
        int code = GsonUtils.getIntFromJSON(s, "err_code");
        if (code==0) {
            Log.e("okhttp:", "isSuccess = true 数据返回正常");
            callBack.onOk(GsonUtils.getStringFromJSON(s, "err_msg"));
        } else {
            String msg = GsonUtils.getStringFromJSON(s, "err_msg");
            if(!TextUtils.isEmpty(msg)){
//                LogCatUtil.e("okhttp:", "isSuccess = true 数据返回失败:"+msg);
                callBack.onError(msg);
            }else{
                callBack.onError("数据返回异常:"+msg);
            }
        }
    }

    private void print(String s, Map<String, String> params) {
        Log.e("okhttp:", "requestParams： " + GsonUtils.serializedToJson(params));
        Log.e("okhttp:", "response= " + s);
        Log.e("okhttp:", "*                                                             *");
    }

/*    public void upLoadImage(final String api, File file, final OnRequestCallBack callBack) {
        final Map<String, String> params = new HashMap<>();
        params.put("key", BaseUserHelper.getCurrentToken());
        params.put("client", "android");
        OkGo.post(BasicConfig.INSTANCE.getBaseUrl() + api)//
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params(params)        // 这里可以上传参数
                .params("live_image", file)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //上传成功
                        success(s, BasicConfig.INSTANCE.getBaseUrl(), api, callBack, params);
                    }


                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        LogCatUtil.e("frank", currentSize + ":" + totalSize + ":" + progress + ":" + networkSpeed);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        error(e, BasicConfig.INSTANCE.getBaseUrl(), api, params, callBack);
                    }
                });
    }*/


    public static long getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        return time;

    }




    /*---------------------------------------------------------------------------------------------------------*/
//    private static String getSign(Map<String, String> params, String clientSecret) {
//        String str = "";
//        List<String> list = new ArrayList<>(params.keySet());
//        Collections.sort(list);
//        for (int i = 0; i < list.size(); i++) {
//            str += list.get(i) + params.get(list.get(i));
//        }
//        return AuthUtil.getEncrypt(str, clientSecret);
//    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

        sortMap.putAll(map);

        return sortMap;
    }

    public interface OnRequestCallBack {
        void onOk(String response);

        void onError(String errorMessage);

    }


}
