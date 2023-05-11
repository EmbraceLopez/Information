package com.le.info.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.le.info.R;
import com.le.info.activity.LoginSmsActivity;
import com.le.info.activity.PersonalActivity;
import com.le.info.activity.SearchActivity;
import com.le.info.base.BaseFragment;
import com.le.info.base.CommonFragmentPagerAdapter;
import com.le.info.base.GlideApp;
import com.le.info.utils.Constant;
import com.le.info.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 */
public class InfoFragment extends BaseFragment {

    @BindView(R.id.tab)
    TabLayout mTabLayout;
    @BindView(R.id.iv_photo)
    ImageView mIvHead;
    @BindView(R.id.vp)
    ViewPager mViewPager;

    private List<Fragment> fragmentList;
    private List<String> titleList;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_info;
    }

    public static InfoFragment newInstance(){
        return new InfoFragment();
    }

    @Override
    public void init(View view, Bundle savedInstanceState) {
        initVp();
    }


    @OnClick({R.id.iv_photo,R.id.iv_search})
    public void onViewClicked(View view){
        switch(view.getId()){
            case R.id.iv_photo:
                String token = SPUtils.getPrefString("token", "");
                if (!TextUtils.isEmpty(token)) {
                    startActivity(new Intent(requireActivity(), PersonalActivity.class));
                } else {
                    //未登录
                    startActivity(new Intent(requireActivity(), LoginSmsActivity.class));
                }
                break;
            case R.id.iv_search:
                SearchActivity.start(requireContext());
                break;
        }
    }

    @Override
    public void onResume() {
        setUserInfo();
        super.onResume();
    }

    /**
     * 初始化ViewPager
     */
    private void initVp(){
        CommonFragmentPagerAdapter fragmentAdapter = new CommonFragmentPagerAdapter(getChildFragmentManager());
        fragmentList = new ArrayList<>();
        fragmentList.add(ChoicenessFragment.newInstance());
        fragmentList.add(RecommendFragment.newInstance());
        fragmentAdapter.setFragmentList(fragmentList);
        titleList = new ArrayList<>();
        titleList.add("精选");
        titleList.add("推荐");
        fragmentAdapter.setTitleList(titleList);
        mViewPager.setAdapter(fragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo(){
        String headPicAddress = Constant.fillPicPath(SPUtils.getPrefString("headPicAddress", ""));
        GlideApp.with(requireContext())
                .load(headPicAddress)
                .circleCrop()
                .placeholder(R.drawable.ic_default_head)
                .into(mIvHead);
    }
}