package com.le.info.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.api.UserApi;
import com.le.info.utils.SignUtils;
import com.le.info.widget.VerCodeTextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class GetBackPwdActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.tv_get_code)
    VerCodeTextView mVerCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getback_pwd);
        ButterKnife.bind(this);

        mVerCode.setListener(new VerCodeTextView.OnGetCodeListener() {
            @Override
            public boolean onGet() {
                String phoneNumber = mEtPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toasty.warning(GetBackPwdActivity.this,mEtPhone.getHint().toString()).show();
                    return false;
                }
                //判断号码的正则
                String regex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
                if(!phoneNumber.matches(regex)){
                    Toasty.warning(GetBackPwdActivity.this,"手机号码格式错误").show();
                    return false;
                }
                sendSmsCode(phoneNumber);
                return true;
            }
        });
    }

    @OnClick({R.id.iv_back,R.id.btn_confirm})
    public void onViewClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                getBackPwd();
                break;
        }
    }

    private void sendSmsCode(String phoneNumber){

        //登录发送验证码标记为1
        ApiManager.getInstance().create(UserApi.class).sendSmsCode(phoneNumber,"2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            Toasty.normal(GetBackPwdActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(GetBackPwdActivity.this,GetBackPwdActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getBackPwd(){
        String phoneNumber = mEtPhone.getText().toString().trim();
        String smsCode = mEtCode.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if(TextUtils.isEmpty(phoneNumber)){
            Toasty.warning(GetBackPwdActivity.this,mEtPhone.getHint().toString()).show();
            return;
        }
        if(TextUtils.isEmpty(smsCode)){
            Toasty.warning(GetBackPwdActivity.this,mEtCode.getHint().toString()).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toasty.warning(GetBackPwdActivity.this,mEtPwd.getHint().toString()).show();
            return;
        }

        ApiManager.getInstance().create(UserApi.class)
                .getBackPwd(phoneNumber,smsCode, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            Toasty.normal(GetBackPwdActivity.this,jsonObject.optString("tips")).show();
                            if(jsonObject.optBoolean("success")){
                                onBackPressed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(GetBackPwdActivity.this,GetBackPwdActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }
}
