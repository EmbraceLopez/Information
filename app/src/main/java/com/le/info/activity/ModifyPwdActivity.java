package com.le.info.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.net.ApiManager;
import com.le.info.net.CommonNetObserver;
import com.le.info.net.api.UserApi;
import com.le.info.utils.SPUtils;
import com.le.info.widget.VerCodeTextView;

import org.bouncycastle.math.raw.Mod;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ModifyPwdActivity extends BaseActivity {

    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.et_confirm_pwd)
    EditText mEtConfirmPwd;
    @BindView(R.id.tv_get_code)
    VerCodeTextView mVerCode;

    private String phoneNumber = SPUtils.getPrefString("phoneNumber", "");

    public static void start(Context context, String title){
        Intent intent = new Intent(context, ModifyPwdActivity.class);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);

        mTvMainTitle.setText(getIntent().getStringExtra("title"));

        mVerCode.setListener(new VerCodeTextView.OnGetCodeListener() {
            @Override
            public boolean onGet() {
                if (TextUtils.isEmpty(phoneNumber)){
                    Toasty.warning(ModifyPwdActivity.this,"请先登录").show();
                    return false;
                }
                //判断号码的正则
                String regex = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
                if(!phoneNumber.matches(regex)){
                    Toasty.warning(ModifyPwdActivity.this,"手机号码格式错误").show();
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

        //登录发送验证码标记为1，修改密码标记为2
        ApiManager.getInstance().create(UserApi.class).sendSmsCode(phoneNumber,"2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonNetObserver<ResponseBody>(this,getLoadingDialog(),compositeDisposable) {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            Toasty.normal(ModifyPwdActivity.this,jsonObject.optString("tips")).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(ModifyPwdActivity.this,ModifyPwdActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

    private void getBackPwd(){
        String confirmPwd = mEtConfirmPwd.getText().toString().trim();
        String smsCode = mEtCode.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if(TextUtils.isEmpty(smsCode)){
            Toasty.warning(ModifyPwdActivity.this,mEtCode.getHint().toString()).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toasty.warning(ModifyPwdActivity.this,mEtPwd.getHint().toString()).show();
            return;
        }
        if(TextUtils.isEmpty(confirmPwd)){
            Toasty.warning(ModifyPwdActivity.this,mEtConfirmPwd.getHint().toString()).show();
            return;
        }

        if(!pwd.equals(confirmPwd)){
            Toasty.warning(ModifyPwdActivity.this,"两次密码输入不一致").show();
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
                            Toasty.normal(ModifyPwdActivity.this,jsonObject.optString("tips")).show();
                            if(jsonObject.optBoolean("success")){
                                onBackPressed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toasty.error(ModifyPwdActivity.this,ModifyPwdActivity.this.getResources().getString(R.string.parse_error)).show();
                        }
                    }
                });
    }

}
