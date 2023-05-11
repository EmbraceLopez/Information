package com.le.info.base;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.DialogSettings;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MyApplication extends MultiDexApplication {

    private static MyApplication application;

    private static Gson gson;

    //activity集合，用于管理所有的Activity
    public static List<Activity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        activityList = new LinkedList<>();

        init();
    }

    private void init(){
        gson = new Gson();

        initDialog();
        setLifeCallBack();
        setComponentCallBack();
    }

    /**
     * 初始化dialog库
     */
    private void initDialog(){
        DialogSettings.style = DialogSettings.STYLE_IOS;
        //DialogSettings.type = DialogSettings.TYPE_IOS;
        DialogSettings.dialog_cancelable_default = true;
        DialogSettings.use_blur = false;
    }

    /**
     * 监听activity生命周期
     */
    private void setLifeCallBack(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                if(null != activity){
                    activityList.add(activity);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                if(null != activityList && activityList.isEmpty()){
                    activityList.remove(activity);
                }
            }
        });
    }

    /**
     * 监听程序内存配置
     */
    private void setComponentCallBack(){
        registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
                    //当应用程序中的所有UI组件全部不可见时，清理缓存，防止被杀
                    //此处加所有使用过缓存的组件，清理其对应的缓存
                    Glide.get(getApplicationContext()).clearMemory();
                }
            }

            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
            }

            @Override
            public void onLowMemory() {
            }
        });
    }

    /**
     * 获取Application
     */
    public static Context getContext(){
        return application;
    }

    public static MyApplication getApplication(){
        return application;
    }

    public static Gson getGson(){
        return gson;
    }

    /**
     * 结束当前所有的activity
     * 一般在退出程序或者退出登录时调用
     */
    public static void clearActivities(){
        ListIterator<Activity> iterator = activityList.listIterator();
        Activity activity;
        while (iterator.hasNext()){
            activity = iterator.next();
            if(activity != null){
                activity.finish();
            }
        }
    }

    /**
     * 获取当前的Activity
     * @return
     */
    public static Activity getCurrentActivity(){
        if(null != activityList && !activityList.isEmpty()){
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }

    /**
     * 退出程序
     */
    public static void quiteApplication(){
        clearActivities();
    }

}
