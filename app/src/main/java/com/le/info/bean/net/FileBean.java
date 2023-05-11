package com.le.info.bean.net;

import com.le.info.bean.BaseBean;

import java.util.List;

public class FileBean extends BaseBean {

    private String success;
    private String tips;
    private List<FileSubBean> file;

    public List<FileSubBean> getFile() {
        return file;
    }

    public void setFile(List<FileSubBean> file) {
        this.file = file;
    }

    @Override
    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public static class FileSubBean {

        private String name;
        private String path;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
