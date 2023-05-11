package com.le.info.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.api.UserApi;
import com.le.info.utils.SPUtils;
import com.le.info.utils.SignUtils;
import com.le.info.utils.StatusBarUtil;
import com.le.info.widget.VerCodeTextView;

import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LoginPwdActivity  extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.linear_privacy)
    LinearLayout mLinearPrivacy;
    @BindView(R.id.check_privacy)
    CheckBox mCheckPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back,R.id.btn_login,R.id.tv_get_pwd})
    public void onViewClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_get_pwd:
                startActivity(new Intent(LoginPwdActivity.this, GetBackPwdActivity.class));
                break;
        }
    }

    /**
     * 密码登录
     */
    private void login(){
        String phoneNumber = mEtPhone.getText().toString();
        String pwd = mEtPwd.getText().toString();
        if(phoneNumber.isEmpty()){
            Toasty.warning(LoginPwdActivity.this,mEtPhone.getHint().toString()).show();
            return;
        }
        if(pwd.isEmpty()){
            Toasty.warning(LoginPwdActivity.this,mEtPwd.getHint().toString()).show();
            return;
        }
//        if(!mCheckPrivacy.isChecked()){
//            Animation shake = AnimationUtils.loadAnimation(LoginPwdActivity.this, R.anim.shake);
//            mLinearPrivacy.startAnimation(shake);
//            return;
//        }

        ApiManager.getInstance().create(UserApi.class)
                .pwdLogin(phoneNumber, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.optBoolean("success")){
                                JSONObject field = jsonObject.getJSONObject("result");
                                Iterator<String> iterator = field.keys();
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    SPUtils.setPrefString(key, field.getString(key));
                                }
                                SPUtils.setPrefString("token",jsonObject.optString("token"));
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(LoginPwdActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Toasty.normal(LoginPwdActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(LoginPwdActivity.this,LoginPwdActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

}
