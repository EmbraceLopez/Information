package com.le.info.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PostUtils {

    /**
     * 生成文字部分
     *
     * @param value 文字
     * @return 请求体
     */
    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    /**
     * 上传个文件
     * 生成请求体
     *
     * @return 请求体
     */
    public static List<MultipartBody.Part> toRequestBody(List<File> files, List<String> uploadNames) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(uploadNames.get(i), file.getName(), requestFile);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 上传单个文件
     * @param file
     * @param uploadName
     * @return
     */
    public static MultipartBody.Part toSingleRequestBody(File file, String uploadName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(uploadName, file.getName(), requestFile);
        return part;
    }

}