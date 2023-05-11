package com.le.info.net;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.kongzue.dialog.v2.WaitDialog;
import com.le.info.R;
import com.le.info.bean.BaseBean;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class SimpleNetObserver<T extends BaseBean> implements Observer<T> {

    private Context mContext;
    private Dialog mDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable mCompositeDisposable;

    public SimpleNetObserver(Context context,CompositeDisposable compositeDisposable) {
        mContext = context;
        mCompositeDisposable = compositeDisposable;
    }

    public SimpleNetObserver(Context context,Dialog dialog, CompositeDisposable compositeDisposable) {
        mDialog = dialog;
        mContext = context;
        mCompositeDisposable = compositeDisposable;
    }

    public SimpleNetObserver(Context context,SwipeRefreshLayout swipeRefreshLayout, CompositeDisposable compositeDisposable) {
        mContext = context;
        mSwipeRefreshLayout = swipeRefreshLayout;
        mCompositeDisposable = compositeDisposable;
    }


    @Override
    public void onSubscribe(Disposable d) {
        mCompositeDisposable.add(d);
        showLoading();
    }


    abstract public void onSuccess(T response);

    public void onFail(BaseBean response) {
        Toasty.error(mContext,response.getTips()).show();
    }

    @Override
    public void onNext(T response) {
        if ("true".equals(response.getSuccess())) {
            onSuccess(response);
        } else {
            onFail(response);
        }
    }

    @Override
    public void onComplete() {
        dismissLoading();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        dismissLoading();
        if (e instanceof HttpException) {     //   HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
    }

    protected void showLoading(){
        if (mDialog != null) {
            mDialog.show();
        }
        if (mSwipeRefreshLayout != null) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }
    }

    protected void dismissLoading() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
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

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }

}