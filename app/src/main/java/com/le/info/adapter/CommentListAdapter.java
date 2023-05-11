package com.le.info.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.le.info.R;
import com.le.info.base.GlideApp;
import com.le.info.bean.net.CommentListBean;
import com.le.info.utils.Constant;
import com.le.info.utils.DateUtils;

import java.util.Date;

public class CommentListAdapter extends BaseQuickAdapter<CommentListBean.ResultBean, BaseViewHolder> {

    public CommentListAdapter() {
        super(R.layout.item_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentListBean.ResultBean item) {
        GlideApp.with(mContext)
                .load(Constant.fillPicPath(item.getHeadPicAddress()))
                .placeholder(R.drawable.ic_default_head2)
                .circleCrop()
                .into((ImageView) helper.getView(R.id.iv_head));

        helper.setText(R.id.tv_username, item.getUsername())
                .setText(R.id.tv_content, item.getContent())
                .setText(R.id.tv_time, item.getPublishTime());
    }

}

