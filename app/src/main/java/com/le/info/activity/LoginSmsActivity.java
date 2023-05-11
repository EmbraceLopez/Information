package com.le.info.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.api.UserApi;
import com.le.info.utils.SPUtils;
import com.le.info.utils.SignUtils;
import com.le.info.utils.StatusBarUtil;
import com.le.info.utils.SystemUtils;
import com.le.info.widget.VerCodeTextView;

import org.json.JSONObject;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LoginSmsActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.tv_get_code)
    VerCodeTextView mVerCode;
    @BindView(R.id.linear_login_pwd)
    LinearLayout mLinearLoginPwd;
    @BindView(R.id.linear_login_wx)
    LinearLayout mLinearLoginWx;
    @BindView(R.id.linear_login_qq)
    LinearLayout mLinearLoginQq;
    @BindView(R.id.linear_privacy)
    LinearLayout mLinearPrivacy;
    @BindView(R.id.check_privacy)
    CheckBox mCheckPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_login);
        ButterKnife.bind(this);

        mVerCode.setListener(new VerCodeTextView.OnGetCodeListener() {
            @Override
            public boolean onGet() {
                String phoneNumber = mEtPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toasty.warning(LoginSmsActivity.this,mEtPhone.getHint().toString()).show();
                    return false;
                }
                //判断号码的正则
                String regex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
                if(!phoneNumber.matches(regex)){
                    Toasty.warning(LoginSmsActivity.this,"手机号码格式错误").show();
                    return false;
                }
                sendSmsCode(phoneNumber);
                return true;
            }
        });
    }

    @OnClick({R.id.btn_login,R.id.linear_login_pwd,R.id.linear_login_wx,R.id.linear_login_qq,R.id.iv_back})
    public void onViewClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.linear_login_pwd:
                startActivity(new Intent(this, LoginPwdActivity.class));
                break;
            case R.id.linear_login_wx:
            case R.id.linear_login_qq:
                Toasty.normal(LoginSmsActivity.this,"即将上线").show();
                break;
        }
    }

    /**
     * 登录
     */
    private void login(){

        String phoneNumber = mEtPhone.getText().toString().trim();
        String smsCode = mEtCode.getText().toString().trim();

        if(TextUtils.isEmpty(phoneNumber)){
            Toasty.warning(LoginSmsActivity.this,mEtPhone.getHint().toString()).show();
            return;
        }
        if(TextUtils.isEmpty(smsCode)){
            Toasty.warning(LoginSmsActivity.this,mEtCode.getHint().toString()).show();
            return;
        }
//        if(!mCheckPrivacy.isChecked()){
//            Animation shake = AnimationUtils.loadAnimation(LoginSmsActivity.this, R.anim.shake);
//            mLinearPrivacy.startAnimation(shake);
//            return;
//        }

        ApiManager.getInstance().create(UserApi.class)
                .smsLogin(phoneNumber,smsCode)
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
                                intent.setClass(LoginSmsActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Toasty.normal(LoginSmsActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(LoginSmsActivity.this,LoginSmsActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void sendSmsCode(String phoneNumber){

        //登录发送验证码标记为1
        ApiManager.getInstance().create(UserApi.class).sendSmsCode(phoneNumber,"1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            Toasty.normal(LoginSmsActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(LoginSmsActivity.this,LoginSmsActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

}
