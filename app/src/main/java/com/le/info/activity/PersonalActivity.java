package com.le.info.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.base.GlideApp;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.api.UserApi;
import com.le.info.utils.Constant;
import com.le.info.utils.SPUtils;
import com.le.info.utils.StatusBarUtil;
import com.le.info.widget.BigImgPopWindow;

import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PersonalActivity extends BaseActivity {

    @BindView(R.id.iv_head_pic)
    ImageView mIvHeadPic;

    private String headPicAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void setMsg(){
        headPicAddress = Constant.fillPicPath(SPUtils.getPrefString("headPicAddress", ""));
        GlideApp.with(PersonalActivity.this)
                .load(headPicAddress)
                .circleCrop()
                .placeholder(R.drawable.ic_default_head2)
                .into(mIvHeadPic);
    }

    public static void start(Context context){
        Intent intent = new Intent(context,PersonalActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_back,R.id.linear_modify_msg,R.id.linear_set,R.id.iv_head_pic})
    public void onClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_head_pic:
                if(!TextUtils.isEmpty(headPicAddress)){
                    new BigImgPopWindow()
                            .setActivity(this)
                            .setAnchorView(mIvHeadPic)
                            .setImgUrl(headPicAddress)
                            .build()
                            .show();
                }
                break;
            case R.id.linear_modify_msg:
                MyMsgActivity.start(PersonalActivity.this,"编辑资料");
                break;
            case R.id.linear_my_collect:
                break;
            case R.id.linear_browse_history:
                break;
            case R.id.linear_set:
                SettingActivity.start(this,"设置");
                break;
        }
    }

    private void getUserInfo(){
        String phoneNumber = SPUtils.getPrefString("phoneNumber", "");
        ApiManager.getInstance().create(UserApi.class)
                .getUser(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(PersonalActivity.this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if (jsonObject.optBoolean("success")) {
                                JSONObject field = jsonObject.getJSONObject("result");
                                Iterator<String> iterator = field.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    SPUtils.setPrefString(key, field.getString(key));
                                }
                                setMsg();
                            } else {
                                Toasty.error(PersonalActivity.this,jsonObject.optString("tips")).show();
                            }
                        } catch (Exception e) {
                            Toasty.error(PersonalActivity.this,PersonalActivity.this.getResources().getString(R.string.parse_error)).show();
                            e.printStackTrace();
                        }
                    }
                });
    }
}
