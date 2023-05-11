package com.le.info.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kongzue.dialog.v2.SelectDialog;
import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.base.MyApplication;
import com.le.info.utils.DataCleanManager;
import com.le.info.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mTvMainTitle.setText(getIntent().getStringExtra("title"));
    }

    public static void start(Context context,String title){
        Intent intent = new Intent(context,SettingActivity.class);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @OnClick({R.id.iv_back,R.id.linear_modify_pwd,R.id.linear_about,R.id.linear_login_out})
    public void onClicked(View view){
        switch(view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.linear_modify_pwd:
                ModifyPwdActivity.start(SettingActivity.this,"修改密码");
                break;
            case R.id.linear_about:
                AboutActivity.start(SettingActivity.this,"关于我们");
                break;
            case R.id.linear_login_out:
                SelectDialog.show(SettingActivity.this, "温馨提示", "您确定要退出登录吗？",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exitApp(SettingActivity.this);
                            }
                        });
                break;
        }
    }

    //退出app,清空信息
    public static void exitApp(Context context) {
        //清除缓存
        SPUtils.clearPreference();
        DataCleanManager.cleanInternalCache(context);  //清理缓存
        DataCleanManager.cleanSharedPreference(context);

        MyApplication.clearActivities();
        context.startActivity(new Intent(context, SplashActivity.class));
    }

}
