package com.le.info.net;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.le.info.base.MyApplication;
import com.le.info.utils.Constant;
import com.le.info.utils.SPUtils;
import com.le.info.utils.SignUtils;
import com.le.info.utils.SystemUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器
 */
public class CustomerInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl;

        String sign = "";
        String deviceId = SystemUtils.getAndroidId(MyApplication.getApplication());
        //所有接口都需要验签，签名：androidId+appSecret
        try{
            sign = SignUtils.MD5(deviceId + Constant.APP_SECRET);
        } catch (Exception e){
            e.printStackTrace();
        }

        String token = SPUtils.getPrefString("token","");
        if(!TextUtils.isEmpty(token)){
            //需要登录后才能进行的操作，需要带上token验证
            httpUrl = request.url().newBuilder()
                    .addQueryParameter("token",token)
                    .addQueryParameter("deviceId",deviceId)
                    .addQueryParameter("sign",sign)
                    .build();
        } else {
            //游客浏览则无需token
            httpUrl = request.url().newBuilder()
                    .addQueryParameter("deviceId",deviceId)
                    .addQueryParameter("sign",sign)
                    .build();
        }

        Log.i("httpUrl---",httpUrl.toString());
        request = request.newBuilder().url(httpUrl).build();
        return chain.proceed(request);
    }
}

