package com.le.info.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

/**
 * 尺寸大小实用工具类
 */
public class RuleUtils {

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 将dp转换成对应的像素值
     *
     * @param context
     * @param dp
     * @return
     */
    public static float convertDp2Px(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /**
     * 将sp转换成对应的像素值
     *
     * @param context
     * @param sp
     * @return
     */
    public static float convertSp2Px(Context context, int sp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    /**
     * 将TabLayout下划线与文字等宽
     * @param context context
     * @param tabs tabLayout
     */
    public static void setIndicator(Context context,TabLayout tabs) {
        try {
            Class<?> tabLayout = tabs.getClass();
            Field tabStrip = null;
            try {
                tabStrip = tabLayout.getDeclaredField("mTabStrip");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            tabStrip.setAccessible(true);
            LinearLayout ll_tab = (LinearLayout) tabStrip.get(tabs);
            int ll_tabWidth = ll_tab.getWidth();
            if (ll_tabWidth==0) {
                //若界面未绘制完成，控件宽高为0，需要手动调用measure()获取测量宽高
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        getScreenWidth(context), View.MeasureSpec.EXACTLY);
                ll_tab.measure(widthMeasureSpec,0);
                ll_tabWidth = ll_tab.getMeasuredWidth();
            }

            for (int i = 0; i < ll_tab.getChildCount(); i++) {
                View child = ll_tab.getChildAt(i);
                //用反射取TabView 中的 TextView
                Field mTextViewField = child.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);
                TextView mTextView = (TextView) mTextViewField.get(child);

                //测量mTextView的宽度
                int width = mTextView.getWidth();
                if (width == 0) {
                    mTextView.measure(0, 0);
                    width = mTextView.getMeasuredWidth();
                }
                //计算每一个tab的左右间距
                int preWidth = (ll_tabWidth / ll_tab.getChildCount() - width) / 2;

                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                //保证不被挤压
                params.width = width;
                //动态设置左右margin
                params.leftMargin = preWidth;
                params.rightMargin = preWidth;
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
