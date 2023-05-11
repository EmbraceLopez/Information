package com.le.info.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Locale;

/**
 * 获取验证码的TextView
 */
public class VerCodeTextView extends AppCompatTextView implements View.OnClickListener {

    //倒计时
    private int countDownTime = 60;

    //计时器
    CountDownTimer timer;

    public interface OnGetCodeListener{
        boolean onGet();
    }

    private OnGetCodeListener mOnGetCodeListener;

    public void setListener(OnGetCodeListener listener){
        mOnGetCodeListener = listener;
    }

    public VerCodeTextView(@NonNull Context context) {
        this(context,null);
    }

    public VerCodeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerCodeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mOnGetCodeListener != null){
            boolean isGet = mOnGetCodeListener.onGet();
            if(isGet){
                setEnabled(false);
                if(timer != null){
                    timer.cancel();
                    timer = null;
                }

                cutDown();
            }
        }
    }

    private void cutDown(){
        timer = new CountDownTimer(countDownTime * 1000L,1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(String.format(Locale.CHINA,"%ds后重试",millisUntilFinished / 1000L));
                countDownTime = (int) (millisUntilFinished / 1000L);
            }

            @Override
            public void onFinish() {
                setText("获取验证码");
                countDownTime = 60;
                setEnabled(true);
            }
        };
        timer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(timer != null){
            timer.cancel();
        }
    }
}
