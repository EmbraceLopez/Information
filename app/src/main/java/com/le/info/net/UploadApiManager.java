package com.le.info.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.le.info.utils.Constant;
import com.safframework.http.interceptor.AndroidLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import cn.netdiscovery.http.interceptor.LoggingInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 封装网络请求
 */
public class UploadApiManager {

    private Retrofit retrofit;

    //初始化请求
    private UploadApiManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (Constant.APP_DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        //拦截器3
        LoggingInterceptor loggingInterceptor3 = AndroidLoggingInterceptor.build();

        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor3)
                .build();

        //转换日期
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();

        OkHttpClient client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    //静态内部类
    private static class SingletonHolder {
        private static final UploadApiManager INSTANCE = new UploadApiManager();
    }

    //获取单例
    public static UploadApiManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //获取retrofit
    public Retrofit getRetrofit() {
        return retrofit;
    }

    //创建api
    public <T> T create(Class<T> cls) {
        return getRetrofit().create(cls);
    }

}