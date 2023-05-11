package com.le.info.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.le.info.R;
import com.le.info.activity.H5Activity;
import com.le.info.adapter.InfoListAdapter;
import com.le.info.base.BaseFragment;
import com.le.info.bean.net.InfoListBean;
import com.le.info.net.ApiManager;
import com.le.info.net.ListNetObserver;
import com.le.info.net.api.InfoApi;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 推荐
 */
public class RecommendFragment extends BaseFragment {

    @BindView(R.id.swipe)
    SwipeRefreshLayout mSw;
    @BindView(R.id.rv)
    RecyclerView mRv;

    private int cPage = 1;
    private int pageSize = 8;

    private InfoListAdapter mAdapter;

    public static RecommendFragment newInstance(){
        return new RecommendFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void init(View view, Bundle savedInstanceState) throws Exception {
        mAdapter = new InfoListAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRv);
        mAdapter.setEmptyView(R.layout.layout_empty, mRv);
        getList(1);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                H5Activity.start(requireContext(),mAdapter.getData().get(position).getInfoUrl(),mAdapter.getData().get(position).getId());
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getList(cPage + 1);
            }
        }, mRv);

        //刷新
        mSw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getList(1);
            }
        });
    }

    private void getList(final int page) {
        ApiManager.getInstance().create(InfoApi.class)
                .getInfo(page, pageSize, "2", "")
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ListNetObserver<InfoListBean>(requireContext(),mSw,compositeDisposable,page,mAdapter){
                    @Override
                    public void updatePage(int page) {
                        super.updatePage(page);
                        cPage = page;
                    }
                });
    }
}