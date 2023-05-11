package com.le.info.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.le.info.R;
import com.le.info.base.BaseActivity;
import com.le.info.base.MyApplication;
import com.le.info.fragment.InfoFragment;
import com.le.info.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.frame_container)
    FrameLayout mFrameContainer;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private static long beforeTime = 0;  //退出应用时第一次点击返回键的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initFragments();
        switchPages(0);

        StatusBarUtil.transparentStatusBar(this);
        StatusBarUtil.setStatusBarFontIconDark(this, false);
    }

    private void initFragments(){
        mFragmentList.add(InfoFragment.newInstance());  //首页
    }

    private void switchPages(int index){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment;
        for(int i = 0;i < mFragmentList.size();i++){
            if(i == index){
                continue;
            }
            fragment = mFragmentList.get(i);
            if(fragment.isAdded()){
                transaction.hide(fragment);
            }
        }
        fragment = mFragmentList.get(index);
        if(fragment.isAdded()){
            transaction.show(fragment);
        } else {
            transaction.add(R.id.frame_container,fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - beforeTime < 2000){
            MyApplication.quiteApplication();
        } else {
            Toasty.normal(this,"再按一次退出").show();
        }
        beforeTime = currentTime;
    }

    private void getUserLike(){

    }
}