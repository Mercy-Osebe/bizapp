package com.test.bizapp.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BizService {

    @FormUrlEncoded
    @POST("auth/register")
    Call<String> signUp(
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("auth/login")
    Call<String> login(
            @Field("email") String email,
            @Field("password") String password
    );
}
