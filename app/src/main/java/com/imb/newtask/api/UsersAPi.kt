package com.imb.newtask.api

import com.imb.newtask.pojos.UserLogin
import com.imb.newtask.pojos.UserRegister
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UsersAPi {

    @FormUrlEncoded
    @POST("api/development/users/login/")
    fun login(@Field("username") userName: String, @Field("password") password: String): Call<UserLogin>

    @POST("api/development/users/register/")
    fun register(@Body user: UserRegister): Call<UserRegister>
}