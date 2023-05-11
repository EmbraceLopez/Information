package com.le.info.net.api;

import com.le.info.bean.net.FileBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CommonApi {

    /**
     * 多文件上传
     * @param parts
     * @return
     */
    @Multipart
    @POST("uploadFile")
    Observable<FileBean> uploadFile(@Part() List<MultipartBody.Part> parts);


    @GET("executeLike")
    Observable<ResponseBody> executeLike(@Query("infoId") String infoId,
                                         @Query("userId") String userId);

    @GET("getInfoLike")
    Observable<ResponseBody> getInfoLike(@Query("infoId") String infoId,
                                         @Query("userId") String userId);

    @GET("executeCollect")
    Observable<ResponseBody> executeCollect(@Query("infoId") String infoId,
                                         @Query("userId") String userId);

    @GET("getInfoCollect")
    Observable<ResponseBody> getInfoCollect(@Query("infoId") String infoId,
                                         @Query("userId") String userId);
}
