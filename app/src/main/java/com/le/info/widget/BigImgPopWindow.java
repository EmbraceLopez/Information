package com.le.info.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.le.info.R;
import com.le.info.base.GlideApp;
import com.le.info.utils.RuleUtils;

public class BigImgPopWindow {
    private Activity mActivity;
    private View mAnchorView;
    private SmartPopupWindow mPopupWindow;
    //网络地址,或本地路径
    private Object imgUrl;

    public BigImgPopWindow build() {
        View root = LayoutInflater.from(mActivity).inflate(R.layout.pop_big_img, null);
        PhotoView photoView = root.findViewById(R.id.photoView);

        mPopupWindow = SmartPopupWindow.Builder.build(mActivity, root)
                .setAlpha(0.4f)
                .setOutsideTouchDismiss(true)
                .setAnimationStyle(R.style.PopScaleAnimStyle)
                .setSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .createPopupWindow();

        //因为popupWindow会修改为Wrap所以要自己设置大小
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RuleUtils.getScreenWidth(mActivity), RuleUtils.getScreenHeight(mActivity));
        photoView.setLayoutParams(lp);

        GlideApp.with(photoView)
                .load(imgUrl)
                .fitCenter()
                .into(photoView);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        return this;
    }

    public BigImgPopWindow setActivity(Activity activity) {
        mActivity = activity;
        return this;
    }

    public BigImgPopWindow setAnchorView(View anchorView) {
        mAnchorView = anchorView;
        return this;
    }

    public BigImgPopWindow setImgUrl(Object imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.showAtAnchorView(mAnchorView, VerticalPosition.CENTER, HorizontalPosition.CENTER);
        }
    }

}
