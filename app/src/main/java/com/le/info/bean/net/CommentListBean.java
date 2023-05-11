package com.le.info.bean.net;

import com.le.info.bean.BaseListBean;

import java.util.List;

public class CommentListBean extends BaseListBean {

    private String success;
    private String tips;
    private List<ResultBean> result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean{
        private int id;
        private String userId;
        private String infoId;
        private String content;
        private String publishTime;
        private String username; //user表中username以及头像地址
        private String headPicAddress;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getInfoId() {
            return infoId;
        }

        public void setInfoId(String infoId) {
            this.infoId = infoId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getHeadPicAddress() {
            return headPicAddress;
        }

        public void setHeadPicAddress(String headPicAddress) {
            this.headPicAddress = headPicAddress;
        }
    }
}
