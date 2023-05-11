package com.le.info.net;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.kongzue.dialog.v2.WaitDialog;
import com.le.info.R;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class CommonNetObserver<T> implements Observer<T> {

    private SwipeRefreshLayout mSwipeRef;
    private CompositeDisposable mCompositeDisposable;
    private Dialog mDialog;
    private Context mContext;

    public CommonNetObserver(Context context,CompositeDisposable compositeDisposable){
        mContext = context;
        mCompositeDisposable = compositeDisposable;
    }

    public CommonNetObserver(Context context,Dialog dialog, CompositeDisposable compositeDisposable) {
        mContext = context;
        mDialog = dialog;
        mCompositeDisposable = compositeDisposable;
    }

    public CommonNetObserver(Context context,SwipeRefreshLayout swipeRefreshLayout, CompositeDisposable compositeDisposable) {
        mContext = context;
        mSwipeRef = swipeRefreshLayout;
        mCompositeDisposable = compositeDisposable;
    }

    public CommonNetObserver(){

    }

    private void dismissLoading() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mSwipeRef != null) {
            mSwipeRef.setRefreshing(false);
        }
    }

    private void showLoading() {
        if (mDialog != null) {
            mDialog.show();
        }
        if (mSwipeRef != null) {
            if (!mSwipeRef.isRefreshing()) {
                mSwipeRef.setRefreshing(true);
            }
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mCompositeDisposable.add(d);

        showLoading();
    }

    @Override
    public void onComplete() {
        dismissLoading();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        dismissLoading();
        if (e instanceof HttpException) {     //HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);  //未知错误
        }
    }

    /**
     * 请求异常
     */
    public void onException(ExceptionReason reason){
        switch (reason){
            case CONNECT_ERROR:
                Toasty.error(mContext, mContext.getResources().getString(R.string.connect_error));
                break;
            case CONNECT_TIMEOUT:
                Toasty.error(mContext, mContext.getResources().getString(R.string.connect_timeout));
                break;
            case BAD_NETWORK:
                Toasty.error(mContext, mContext.getResources().getString(R.string.bad_network));
                break;
            case PARSE_ERROR:
                Toasty.error(mContext, mContext.getResources().getString(R.string.parse_error));
                break;
            case UNKNOWN_ERROR:
            default:
                Toasty.error(mContext, mContext.getResources().getString(R.string.unknown_error));
                break;
        }
    }

    public enum ExceptionReason {
        //解析数据失败
        PARSE_ERROR,
        //网络问题
        BAD_NETWORK,
        //连接错误
        CONNECT_ERROR,
        //连接超时
        CONNECT_TIMEOUT,
        //未知错误
        UNKNOWN_ERROR,
    }
}
