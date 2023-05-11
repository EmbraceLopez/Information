package com.le.info.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.le.info.R;
import com.le.info.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_main_title)
    TextView mTvMainTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mTvMainTitle.setText(getIntent().getStringExtra("title"));
    }

    public static void start(Context context, String title){
        Intent intent = new Intent(context,AboutActivity.class);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

}
