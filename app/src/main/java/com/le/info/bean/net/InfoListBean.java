package com.le.info.bean.net;

import com.le.info.bean.BaseListBean;

import java.util.List;

public class InfoListBean extends BaseListBean {

    /**
     * {"result":[{"id":1,"title":"10 × 10 的正方形最多可放入多少个直径为 1 的圆？","content":null,"dateline":"2022-10-25 20:19:36.0","infoUrl":"https://daily.zhihu.com/story/9754022","infoType":"1","infoPicAddress":"/images/20221025204702.jpg","collectCount":0,"likeCount":0,"commentCount":0,"glanceCount":0},
     *            {"id":21,"title":"近视眼在老了以后真的会恢复并且中和老花眼吗？","content":null,"dateline":"2022-10-14 20:40:28.0","infoUrl":"https://daily.zhihu.com/story/9754008","infoType":"1","infoPicAddress":"/images/20221025204202.jpg","collectCount":0,"likeCount":0,"commentCount":0,"glanceCount":0},
     * "success":"true",
     * "tips":"查询成功"
     * }
     */

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
        /**
         * {"id":21,
         * "title":"近视眼在老了以后真的会恢复并且中和老花眼吗？",
         * "content":null,
         * "dateline":"2022-10-14 20:40:28.0",
         * "infoUrl":"https://daily.zhihu.com/story/9754008",
         * "infoType":"1",
         * "infoPicAddress":"/images/20221025204202.jpg",
         * "collectCount":0,
         * "likeCount":0,
         * "commentCount":0,
         * "glanceCount":0
         * },
         */
        private int id;
        private String title;
        private String content;
        private String dateline;
        private String infoUrl;
        private String infoType;
        private String infoPicAddress;
        private int collectCount;
        private int likeCount;
        private int commentCount;
        private int glanceCount;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getInfoUrl() {
            return infoUrl;
        }

        public void setInfoUrl(String infoUrl) {
            this.infoUrl = infoUrl;
        }

        public String getInfoType() {
            return infoType;
        }

        public void setInfoType(String infoType) {
            this.infoType = infoType;
        }

        public String getInfoPicAddress() {
            return infoPicAddress;
        }

        public void setInfoPicAddress(String infoPicAddress) {
            this.infoPicAddress = infoPicAddress;
        }

        public int getCollectCount() {
            return collectCount;
        }

        public void setCollectCount(int collectCount) {
            this.collectCount = collectCount;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getGlanceCount() {
            return glanceCount;
        }

        public void setGlanceCount(int glanceCount) {
            this.glanceCount = glanceCount;
        }
    }



}
