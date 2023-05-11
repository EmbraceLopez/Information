package com.le.info.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.le.info.R;
import com.le.info.adapter.CommentListAdapter;
import com.le.info.adapter.InfoListAdapter;
import com.le.info.base.BaseActivity;
import com.le.info.bean.net.CommentListBean;
import com.le.info.bean.net.InfoListBean;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.ListNetObserver;
import com.le.info.net.api.CommentApi;
import com.le.info.net.api.CommonApi;
import com.le.info.net.api.InfoApi;
import com.le.info.net.api.UserApi;
import com.le.info.utils.DateUtils;
import com.le.info.utils.SPUtils;
import com.le.info.view.DialogComment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CommentActivity extends BaseActivity {

    @BindView(R.id.tv_main_title)
    TextView mTvTitle;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSw;
    @BindView(R.id.comment_rv)
    RecyclerView mRv;

    private int cPage = 1;
    private int pageSize = 6;
    private String infoId = "";

    private CommentListAdapter mAdapter;

    public static void start(Context context,String title,String infoId){
        Intent intent = new Intent(context,CommentActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("infoId",infoId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        String title = getIntent().getStringExtra("title");
        infoId = getIntent().getStringExtra("infoId");
        mTvTitle.setText(title);

        initRv();
    }

    private void initRv(){
        mAdapter = new CommentListAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        mRv.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRv);
        mAdapter.setEmptyView(R.layout.layout_empty, mRv);
        getList(1);

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

    @OnClick({R.id.iv_back,R.id.linear_comment})
    public void onViewClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.linear_comment:
                comment();
                break;
        }
    }


    private void comment(){
        String token = SPUtils.getPrefString("token", "");
        if (TextUtils.isEmpty(token)) {
            List<String> list = new ArrayList<>();
            list.add("去登录");
            BottomMenu.show(CommentActivity.this, list, new OnMenuItemClickListener() {
                @Override
                public void onClick(String text, int index) {
                    startActivity(new Intent(CommentActivity.this, LoginSmsActivity.class));
                }
            }, true).setTitle("请登录后操作");
            return;
        }

        final DialogComment commentDialog = new DialogComment(this, R.style.CommonDialogStyle);
        commentDialog.setHint("友善评论");
        commentDialog.setListener(new DialogComment.OnTextSubmitListener() {
            @Override
            public void onClick(String content) {
                if (content.isEmpty()){
                    return;
                }
                submitComment(content);
                commentDialog.dismiss();
            }
        });
        commentDialog.show();

    }

    private void submitComment(String content){
        ApiManager.getInstance().create(CommentApi.class)
                .insertComment(SPUtils.getPrefString("id",""), infoId,content, DateUtils.formatDataDetail(new Date(System.currentTimeMillis())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                Toasty.normal(CommentActivity.this,jsonObject.optString("tips")).show();
                                getList(1);
                            }
                            //Toasty.normal(MyMsgActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(CommentActivity.this,CommentActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getList(final int page) {
        ApiManager.getInstance().create(CommentApi.class)
                .getCommentList(page, pageSize, infoId)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ListNetObserver<CommentListBean>(CommentActivity.this,mSw,compositeDisposable,page,mAdapter){
                    @Override
                    public void updatePage(int page) {
                        super.updatePage(page);
                        cPage = page;
                    }
                });
    }

}
