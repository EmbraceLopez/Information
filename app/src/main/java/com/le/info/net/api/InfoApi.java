package com.le.info.net.api;

import com.le.info.bean.net.InfoListBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InfoApi {

    @GET("getInfo")
    Observable<InfoListBean> getInfo(@Query("page") int page,
                                     @Query("pageSize") int pageSize,
                                     @Query("infoType") String infoType,
                                     @Query("keyword") String keyword);

    @GET("getInfoDetail")
    Observable<ResponseBody> getInfoDetail(@Query("infoId") int infoId);


}
