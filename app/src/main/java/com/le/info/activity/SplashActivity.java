package com.le.info.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.le.info.R;
import com.le.info.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        goTo();
    }

    private void goTo() {
        Disposable subscribe = Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        finish();
                    }
                });

        addDisposable(subscribe);
    }
}