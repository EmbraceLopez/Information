package com.le.info.base;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.kongzue.dialog.v2.WaitDialog;
import com.le.info.widget.QMUITipDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseActivity extends AppCompatActivity {

    //rxjava管理
    public CompositeDisposable compositeDisposable;

    protected QMUITipDialog loadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public Dialog getLoadingDialog() {
        if (loadDialog == null) {
            loadDialog = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .create();
        }
        return loadDialog;
    }

    protected void dismissLoadingDialog() {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }

    //添加事件管理，避免开销
    public void addDisposable(Disposable disposable){
        if(compositeDisposable == null){
            compositeDisposable = new CompositeDisposable();
        }

        if(disposable != null && !disposable.isDisposed()){
            compositeDisposable.add(disposable);
        }
    }

    @Override
    protected void onDestroy() {
        if(compositeDisposable != null && compositeDisposable.size() > 0){
            compositeDisposable.clear();
        }
        super.onDestroy();
    }

    //还原字体大小
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration configuration = res.getConfiguration();
        if(configuration.fontScale != 1.0f){
            configuration.fontScale = 1.0f;
            res.updateConfiguration(configuration,res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 判断activity是否处于栈顶
     * @param cls
     * @param context
     * @return
     */
    protected boolean isActivityTop(Class cls, Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name;
        if(manager != null){
            name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
            return name.equals(cls.getName());
        }
        return false;
    }
}
