package com.le.info.net;

import android.app.Dialog;
import android.content.Context;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kongzue.dialog.v2.WaitDialog;
import com.le.info.R;
import com.le.info.bean.BaseListBean;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ListNetObserver<T extends BaseListBean> implements Observer<T> {
    private Context mContext;
    private Dialog mDialog;
    private SwipeRefreshLayout mSwipeRef;
    private CompositeDisposable mCompositeDisposable;
    private int page;
    private BaseQuickAdapter mAdapter;

    public ListNetObserver(Context context,CompositeDisposable compositeDisposable, int page, BaseQuickAdapter adapter) {
        mContext = context;
        mCompositeDisposable = compositeDisposable;
        this.page = page;
        this.mAdapter = adapter;
    }

    public ListNetObserver(Context context,Dialog dialog, CompositeDisposable compositeDisposable, int page, BaseQuickAdapter adapter) {
        mContext = context;
        mDialog = dialog;
        mCompositeDisposable = compositeDisposable;
        this.page = page;
        this.mAdapter = adapter;
    }

    public ListNetObserver(Context context,SwipeRefreshLayout swipeRefreshLayout, CompositeDisposable compositeDisposable, int page, BaseQuickAdapter adapter) {
        mContext = context;
        mSwipeRef = swipeRefreshLayout;
        mCompositeDisposable = compositeDisposable;
        this.page = page;
        this.mAdapter = adapter;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mCompositeDisposable.add(d);

        if (page == 1) {
            showLoading();
        }
    }

    @Override
    public void onNext(T response) {
        dismissLoading();

        if (!"true".equals(response.getSuccess())) {
            Toasty.warning(mContext,response.getTips());
            if (page != 1) {
                mAdapter.loadMoreFail();
            }
            return;
        }

        List results = response.getResult();
        //不需要管长度
        if (page == 1) {
            mAdapter.setNewData(results);
            mAdapter.setEnableLoadMore(true);
            updatePage(1);
        } else {
            if (results == null || results.size() == 0) {
                mAdapter.loadMoreEnd();
                return;
            }
            mAdapter.addData(results);
            mAdapter.loadMoreComplete();
            updatePage(page);
        }

    }

    //更新页码
    public void updatePage(int page) {

    }

    @Override
    public void onComplete() {
        dismissLoading();
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        dismissLoading();

        if (page != 1) {
            mAdapter.loadMoreFail();
        } else {
            Toasty.error(mContext, mContext.getResources().getString(R.string.bad_network));
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

    private void dismissLoading() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mSwipeRef != null) {
            mSwipeRef.setRefreshing(false);
        }
    }
}

