package com.le.info.net.api;

import com.le.info.bean.net.CommentListBean;
import com.le.info.bean.net.InfoListBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CommentApi {

    @GET("getCommentList")
    Observable<CommentListBean> getCommentList(@Query("page") int page,
                                               @Query("pageSize") int pageSize,
                                               @Query("infoId") String infoId);

    @GET("insertComment")
    Observable<ResponseBody> insertComment(@Query("userId") String userId,
                                           @Query("infoId") String infoId,
                                           @Query("content") String content,
                                           @Query("publishTime") String publishTime);
}
