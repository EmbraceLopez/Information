package com.le.info.net.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

    /**
     * 密码登录
     * @param phone
     * @param pwd 加密传输
     * @return
     */
    @GET("pwdLogin")
    Observable<ResponseBody> pwdLogin(@Query("phoneNumber") String phone,
                                      @Query("pwd") String pwd);

    /**
     * 验证码登录，未注册的号码，验证通过后将进行注册
     * @param phone
     * @param smsCode 验证码
     * @return
     */
    @GET("smsLogin")
    Observable<ResponseBody> smsLogin(@Query("phoneNumber") String phone,
                                      @Query("smsCode") String smsCode);

    /**
     * 发送验证码
     * @param phone
     * @param flag
     * @return
     */
    @GET("sendSmsCode")
    Observable<ResponseBody> sendSmsCode(@Query("phoneNumber") String phone,
                                         @Query("flag") String flag);

    @GET("getBackPwd")
    Observable<ResponseBody> getBackPwd(@Query("phoneNumber") String phone,
                                        @Query("smsCode") String smsCode,
                                        @Query("pwd") String pwd);

    @GET("modifyUser")
    Observable<ResponseBody> modifyUser(@Query("phoneNumber") String phone,
                                        @Query("username") String username,
                                        @Query("gender") String gender,
                                        @Query("birthday") String birthday,
                                        @Query("headPicAddress") String headPicAddress);

    @GET("getUser")
    Observable<ResponseBody> getUser(@Query("phoneNumber") String phone);

    /**
     * 微信或qq登录，若能获取到号码则使用
     * @param phone
     * @param smsCode
     * @return
     */
    @FormUrlEncoded
    @POST("action=wxOrQqLogin")
    Observable<ResponseBody> wxOrQqLogin(@Query("phoneNumber") String phone,
                                      @Query("smsCode") String smsCode);

}
