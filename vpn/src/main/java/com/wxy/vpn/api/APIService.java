package com.wxy.vpn.api;


import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;


/**

 */

public interface APIService {

    @FormUrlEncoded
    @POST("/start")
    void verificationRequest(@Field("api_key") String apiKey,@Field("via") String via, @Field("phone_number") String phone_number,
                             @Field("country_code") String country_code, Callback<TwillioBasicResponse> callback);
  //  https://api.authy.com/protected/json/phones/verification/check?api_key=n8U9fumVsL70iZcRwSKRFy2urXSRDQcS&phone_number=%2B923318058848&country_code=92&verification_code=1234
    @GET("/check")
    void verificationConfirm(@Query("api_key") String apiKey,@Query("phone_number") String phone_number,
                             @Query("country_code") String country_code,@Query("verification_code") String verificationcode, Callback<TwillioBasicResponse> callback);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("device-id") String deviceid,@Field("username") String username, @Field("phone") String phone,
                             @Field("password") String password, Callback<TwillioBasicResponse> callback);
}