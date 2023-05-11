package com.le.info.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.le.info.R;
import com.le.info.adapter.InfoListAdapter;
import com.le.info.base.BaseActivity;
import com.le.info.bean.net.InfoListBean;
import com.le.info.net.ApiManager;
import com.le.info.net.ListNetObserver;
import com.le.info.net.api.InfoApi;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.et_search)
    TextView mEtSearch;
    @BindView(R.id.rv)
    RecyclerView mRv;

    private int cPage = 1;
    private int pageSize = 8;
    private String keyword = "";

    private InfoListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mAdapter = new InfoListAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mRv.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRv);
        mAdapter.setEmptyView(R.layout.layout_empty, mRv);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                H5Activity.start(SearchActivity.this,mAdapter.getData().get(position).getInfoUrl(),mAdapter.getData().get(position).getId());
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getList(cPage + 1);
            }
        }, mRv);

    }

    public static void start(Context context){
        Intent intent = new Intent(context,SearchActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_back,R.id.tv_search})
    public void onClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_search:
                search();
                break;
        }
    }

    private void search(){
        keyword = mEtSearch.getText().toString();
        if(keyword.isEmpty()){
            return;
        }
        getList(1);
    }

    private void getList(final int page) {
        ApiManager.getInstance().create(InfoApi.class)
                .getInfo(page, pageSize, "", keyword)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ListNetObserver<InfoListBean>(SearchActivity.this,getLoadingDialog(),compositeDisposable,page,mAdapter){
                    @Override
                    public void updatePage(int page) {
                        super.updatePage(page);
                        cPage = page;
                    }
                });
    }
}
