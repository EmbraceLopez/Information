package com.le.info.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.le.info.R;
import com.le.info.utils.RuleUtils;

public class DialogComment extends Dialog {

    private EditText mEditText;
    private TextView mTextView;
    private String hint = "友善评论";

    public interface OnTextSubmitListener {
        void onClick(String content);
    }

    private OnTextSubmitListener mListener;

    public void setListener(OnTextSubmitListener listener) {
        mListener = listener;
    }

    public void setHint(String hint) {
        if (!TextUtils.isEmpty(hint)) {
            if (mEditText != null) {
                mEditText.setText("");
                mEditText.setHint(hint);
            }
            this.hint = hint;
        }
    }

    public DialogComment(@NonNull Context context) {
        super(context);

        init();
    }

    public DialogComment(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        init();
    }

    public DialogComment(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        init();
    }

    private void init() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comment, null);
        setContentView(root);
        mEditText = (EditText) root.findViewById(R.id.et_comment_msg);
        mTextView = (TextView) root.findViewById(R.id.tv_post_msg);


        Window window = getWindow();
        WindowManager.LayoutParams layoutParams;
        if (window != null) {
            layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) RuleUtils.convertDp2Px(getContext(), 48);
            window.setAttributes(layoutParams);
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
        }

        mEditText.setText("");
        mEditText.setHint(hint);

        if (mListener != null) {
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = mEditText.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    mListener.onClick(content);
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
