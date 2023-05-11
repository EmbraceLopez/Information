package com.le.info.utils;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Intent意图工具类
 */
public class IntentUtils {

    /**
     * 打开浏览器
     * @param url
     * @return
     */
    public static Intent web(String url){
        if(TextUtils.isEmpty(url)){
            return null;
        }

        Uri uri = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW,uri);
    }

    /**
     * 打开设置
     * @param packageName
     * @return
     */
    public static Intent setting(String packageName){
        if(TextUtils.isEmpty(packageName)){
            return null;
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        return intent;
    }

    /**
     * 回到桌面，后台运行
     * @return
     */
    public static Intent home(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        return intent;
    }
}
