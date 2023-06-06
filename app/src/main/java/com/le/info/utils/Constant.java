package com.le.info.utils;

import android.text.TextUtils;

import androidx.multidex.BuildConfig;

public class Constant {

    //服务器地址
    public static final String SERVER_URL = "http://example.com";

    public static final String BASE_URL = SERVER_URL + "/api/";

    //是否为调试模式
    public static final boolean APP_DEBUG = BuildConfig.DEBUG;

    //验签密钥
    public static final String APP_SECRET= "c37Am244P33u0638S563";

    //完善图片地址
    public static String fillPicPath(String path) {
        String realThumb = null;
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                realThumb = path;
            } else {
                realThumb = Constant.SERVER_URL + "/images/" + path;
            }
        }
        return realThumb;
    }

}
