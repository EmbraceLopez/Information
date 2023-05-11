package com.le.info.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.le.info.R;
import com.le.info.base.GlideApp;
import com.le.info.bean.net.InfoListBean;
import com.le.info.utils.Constant;
import com.le.info.utils.DateUtils;

import java.util.Date;


public class InfoListAdapter extends BaseQuickAdapter<InfoListBean.ResultBean, BaseViewHolder> {

    public InfoListAdapter() {
        super(R.layout.item_info_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, InfoListBean.ResultBean item) {
        GlideApp.with(mContext)
                .load(Constant.SERVER_URL + item.getInfoPicAddress())
                .placeholder(R.drawable.ic_placeholder)
                .into((ImageView) helper.getView(R.id.iv_list_pic));

        helper.setText(R.id.tv_title, item.getTitle())
                .setText(R.id.tv_dateline, item.getDateline());
    }

}
