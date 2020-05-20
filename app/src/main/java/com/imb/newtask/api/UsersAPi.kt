package com.imb.newtask.api

import com.imb.newtask.pojos.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UsersAPi {

    @FormUrlEncoded
    @POST("api/development/users/login/")
    fun login(@Field("username") userName: String, @Field("password") password: String): Call<UserLogin>

    @POST("api/development/users/register/")
    fun register(@Body user: UserRegister): Call<UserRegister>

    @GET("/api/development/users/detail/{id}")
    fun getUserById(@Header("Authorization") token: String, @Path("id") id: Int): Call<User>

    @PUT("/api/development/users/update/{id}/")
    fun updateUserById(@Header("Authorization") token: String, @Path("id") id: Int, @Body user: User): Call<User>

    @GET("/api/development/mansion/list/")
    fun getUserMansionsById(@Header("Authorization") token: String, @Query("owner") ownerID: Int): Call<MansionsResult>

    @GET("/api/development/region/list/")
    fun getRegions(@Header("Authorization") token: String): Call<RegionsApiResult>

    @POST("/api/development/mansion/create/")
    fun createNewMansion(@Header("Authorization") token: String, @Body newMansion: NewMansion): Call<Mansion>

    @PUT("/api/development/mansion/update/{id}/")
    fun updateMansion(@Header("Authorization") token: String, @Path("id") id: Int, @Body newMansion: NewMansion): Call<Mansion>

    @POST("/api/development/facility/create/")
    fun createFacilities(@Header("Authorization") token: String, @Body newFacilities: NewFacilities): Call<NewFacilities>

    @DELETE("/api/development/facility/delete/{id}/")
    fun deleteFacility(@Header("Authorization") token: String, @Path("id") id: Int): Call<Void>

    @Multipart
    @POST("/api/development/picture/create/")
    fun uploadMansionPicture(
        @Header("Authorization") token: String,
        @Part picture: MultipartBody.Part,
        @Part("mansion") mansion: RequestBody
    ): Call<PictureResult>

    @GET("/api/development/mansion/list/")
    fun filterMansionListByRegionAndRoom(
        @Header("Authorization") token: String, @Query("region") regionNum: Int
    ): Call<MansionsResult>

    @POST("/api/development/book/create/")
    fun bookMansion(@Header("Authorization") token: String, @Body bookDetails: BookDetails): Call<BookDetailsResult>

    @FormUrlEncoded
    @POST("/api/development/rate/create/")
    fun rateMansionById(
        @Header("Authorization") token: String, @Field("rate") rating: Int,
        @Field("mansion") mansionId: Int
    ): Call<RatingMansionResult>
}