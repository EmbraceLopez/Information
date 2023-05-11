package com.le.info.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.just.agentweb.AgentWeb;
import com.kongzue.dialog.listener.OnMenuItemClickListener;
import com.kongzue.dialog.v2.BottomMenu;
import com.kongzue.dialog.v2.SelectDialog;
import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.base.GlideApp;
import com.le.info.bean.net.InfoListBean;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.ListNetObserver;
import com.le.info.net.api.CommonApi;
import com.le.info.net.api.InfoApi;
import com.le.info.net.api.UserApi;
import com.le.info.utils.SPUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * H5网页
 */
public class H5Activity extends BaseActivity {

    @BindView(R.id.layout)
    FrameLayout mLayout;
    @BindView(R.id.iv_like)
    ImageView mIvLike;
    @BindView(R.id.tv_like_count)
    TextView mTvLikeCount;
    @BindView(R.id.linear_like)
    LinearLayout mLinearLike;
    @BindView(R.id.iv_collect)
    ImageView mIvCollect;
    @BindView(R.id.tv_collect_count)
    TextView mTvCollectCount;
    @BindView(R.id.linear_collect)
    LinearLayout mLinearCollect;
    @BindView(R.id.iv_comment)
    ImageView mIvComment;
    @BindView(R.id.tv_comment_count)
    TextView mTvCommentCount;

    private AgentWeb mAgentWeb;
    private WebView mWebView;

    private int mInfoId;
    private String mInfoUrl;

    private int commentCount = 0;

    public static void start(Context context, String url,int infoId) {
        Intent starter = new Intent(context, H5Activity.class);
        starter.putExtra("url", url);
        starter.putExtra("infoId", infoId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);
        ButterKnife.bind(this);

        mInfoUrl = getIntent().getStringExtra("url");
        mInfoId = getIntent().getIntExtra("infoId",0);

        initWeb();

        String token = SPUtils.getPrefString("token", "");
        if (!TextUtils.isEmpty(token)) {
            getInfoLike();
            getInfoCollect();
        }
    }



    private void initWeb(){
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLayout, new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(Color.parseColor("#118BD8"))
                .createAgentWeb()
                .ready()
                .go(mInfoUrl);
        //webview基本设置
        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        mWebView = mAgentWeb.getWebCreator().getWebView();
        String ua = mWebView.getSettings().getUserAgentString();
        mWebView.getSettings().setUserAgentString(ua + ";isApp");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);//开启硬件加速
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        mWebView.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAppCachePath(this.getDir("appcache", 0).getPath());
        mWebView.getSettings().setDatabasePath(this.getDir("databases", 0).getPath());
        mWebView.getSettings().setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportMultipleWindows(false);

        //图片适配屏幕大小
        mWebView.setWebViewClient(new ArticleWebViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        getInfoDetail();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mAgentWeb.back()) {
            super.onBackPressed();
        }
    }

    private class ArticleWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //重置webview中img标签的图片大小
            imgReset();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private void imgReset() {
        mWebView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++) " + "{"
                + "var img = objs[i]; "
                + " img.style.width = '100%'; "
                + " img.style.height = 'auto'; "
                + "}" + "})()");
    }

    private void getInfoDetail(){

        ApiManager.getInstance().create(InfoApi.class)
                .getInfoDetail(mInfoId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(H5Activity.this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                commentCount = result.optInt("commentCount");
                                mTvLikeCount.setText(result.optInt("likeCount")+"");
                                mTvCollectCount.setText(result.optInt("collectCount")+"");
                                mTvCommentCount.setText(commentCount+"");
                            } else {
                                Toasty.error(H5Activity.this,jsonObject.optString("tips")).show();
                            }
                        } catch (Exception e) {
                            Toasty.error(H5Activity.this,H5Activity.this.getResources().getString(R.string.parse_error)).show();
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 资讯相关操作
     */

    @OnClick({R.id.iv_back,R.id.linear_like,R.id.linear_collect,R.id.linear_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.linear_like:
                getLike();
                break;
            case R.id.linear_collect:
                getCollect();
                break;
            case R.id.linear_comment:
                CommentActivity.start(H5Activity.this, "评论详情",mInfoId+"");
                break;
        }
    }

    private void getLike(){

        String token = SPUtils.getPrefString("token", "");
        if (TextUtils.isEmpty(token)) {
            List<String> list = new ArrayList<>();
            list.add("去登录");
            BottomMenu.show(H5Activity.this, list, new OnMenuItemClickListener() {
                @Override
                public void onClick(String text, int index) {
                    startActivity(new Intent(H5Activity.this, LoginSmsActivity.class));
                }
            }, true).setTitle("请登录后操作");
            return;
        }

        mLinearLike.setEnabled(false);
        //点赞
        ApiManager.getInstance().create(CommonApi.class)
                .executeLike(mInfoId+"", SPUtils.getPrefString("id",""))
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(H5Activity.this,compositeDisposable) {
                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                int likeState = jsonObject.optInt("likeState");
                                if(likeState == 1){
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_like_true)
                                            .placeholder(R.drawable.ic_like_false)
                                            .into(mIvLike);
                                    mTvLikeCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_like_false)
                                            .placeholder(R.drawable.ic_like_false)
                                            .into(mIvLike);
                                    mTvLikeCount.setTextColor(getResources().getColor(R.color.black));
                                }
                                getInfoDetail();
                            } else {
                                Toasty.error(H5Activity.this,jsonObject.optString("tips")).show();
                            }
                            mLinearLike.setEnabled(true);
                        } catch (Exception e){
                            mLinearLike.setEnabled(true);
                            e.printStackTrace();
                            Toasty.error(H5Activity.this,H5Activity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getCollect(){

        //判断是否登录
        String token = SPUtils.getPrefString("token", "");
        if (TextUtils.isEmpty(token)) {
            List<String> list = new ArrayList<>();
            list.add("去登录");
            BottomMenu.show(H5Activity.this, list, new OnMenuItemClickListener() {
                @Override
                public void onClick(String text, int index) {
                    startActivity(new Intent(H5Activity.this, LoginSmsActivity.class));
                }
            }, true).setTitle("请登录后操作");
            return;
        }

        mLinearCollect.setEnabled(false);

        ApiManager.getInstance().create(CommonApi.class)
                .executeCollect(mInfoId+"", SPUtils.getPrefString("id",""))
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(H5Activity.this,compositeDisposable) {
                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                int collectState = jsonObject.optInt("collectState");
                                if(collectState == 1){
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_collect_true)
                                            .placeholder(R.drawable.ic_collect_false)
                                            .into(mIvCollect);
                                    mTvCollectCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_collect_false)
                                            .placeholder(R.drawable.ic_collect_false)
                                            .into(mIvCollect);
                                    mTvCollectCount.setTextColor(getResources().getColor(R.color.black));
                                }
                                getInfoDetail();
                            } else {
                                Toasty.error(H5Activity.this,jsonObject.optString("tips")).show();
                            }
                            mLinearCollect.setEnabled(true);
                        } catch (Exception e){
                            mLinearCollect.setEnabled(true);
                            e.printStackTrace();
                            Toasty.error(H5Activity.this,H5Activity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getInfoCollect(){
        ApiManager.getInstance().create(CommonApi.class)
                .getInfoCollect(mInfoId+"", SPUtils.getPrefString("id",""))
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(H5Activity.this,compositeDisposable) {
                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                int collectState = jsonObject.optInt("collectState");
                                if(collectState == 1){
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_collect_true)
                                            .into(mIvCollect);
                                    mTvCollectCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_collect_false)
                                            .into(mIvCollect);
                                    mTvCollectCount.setTextColor(getResources().getColor(R.color.black));
                                }
                            } else {
                                Toasty.error(H5Activity.this,jsonObject.optString("tips")).show();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Toasty.error(H5Activity.this,H5Activity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getInfoLike(){
        ApiManager.getInstance().create(CommonApi.class)
                .getInfoLike(mInfoId+"", SPUtils.getPrefString("id",""))
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(H5Activity.this,compositeDisposable) {
                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                int likeState = jsonObject.optInt("likeState");
                                if(likeState == 1){
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_like_true)
                                            .into(mIvLike);
                                    mTvLikeCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                                } else {
                                    GlideApp.with(H5Activity.this)
                                            .load(R.drawable.ic_like_false)
                                            .into(mIvLike);
                                    mTvLikeCount.setTextColor(getResources().getColor(R.color.black));
                                }
                            } else {
                                Toasty.error(H5Activity.this,jsonObject.optString("tips")).show();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            Toasty.error(H5Activity.this,H5Activity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }
}
