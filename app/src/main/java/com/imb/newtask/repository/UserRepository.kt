package com.imb.newtask.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.imb.newtask.api.UsersAPi
import com.imb.newtask.pojos.*
import com.imb.newtask.utils.Constants
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object UserRepository {

    private var userApi: UsersAPi

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()
        val retrofit: Retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL).client(okHttpClient).build()
        userApi = retrofit.create(UsersAPi::class.java)
    }

    fun login(username: String, password: String): MutableLiveData<UserLogin> {
        val userData = MutableLiveData<UserLogin>()

        userApi.login(username, password).enqueue(object : Callback<UserLogin> {
            override fun onFailure(call: Call<UserLogin>, t: Throwable) {
                userData.value = null
            }

            override fun onResponse(call: Call<UserLogin>, response: Response<UserLogin>) {
                userData.value = response.body()
            }
        })
        return userData
    }

    fun register(user: UserRegister): MutableLiveData<UserRegister> {
        val userData = MutableLiveData<UserRegister>()

        userApi.register(
            user
        ).enqueue(object : Callback<UserRegister> {
            override fun onFailure(call: Call<UserRegister>, t: Throwable) {
                userData.value = null

            }

            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                userData.value = response.body()

            }
        })

        return userData
    }

    fun getUserById(token: String, id: Int): MutableLiveData<User> {
        val userData = MutableLiveData<User>()

        userApi.getUserById("Token $token", id).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                userData.value = null

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                userData.value = response.body()

            }

        })

        return userData
    }

    fun updateUserById(token: String, id: Int, user: User): MutableLiveData<User> {
        val userData = MutableLiveData<User>()
        userApi.updateUserById("Token $token", id, user).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                userData.value = null
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                userData.value = response.body()
            }
        })
        return userData
    }

    fun getOwnerMansionById(token: String, id: Int): MutableLiveData<MansionsResult> {
        val mansions = MutableLiveData<MansionsResult>()
        userApi.getUserMansionsById("Token $token", id).enqueue(object : Callback<MansionsResult> {
            override fun onFailure(call: Call<MansionsResult>, t: Throwable) {
                mansions.value = null
            }

            override fun onResponse(
                call: Call<MansionsResult>,
                response: Response<MansionsResult>
            ) {
                mansions.value = response.body()
            }

        })

        return mansions
    }

    fun getRegions(token: String): MutableLiveData<RegionsApiResult> {
        val regions = MutableLiveData<RegionsApiResult>()
        userApi.getRegions("Token $token").enqueue(object : Callback<RegionsApiResult> {
            override fun onFailure(call: Call<RegionsApiResult>, t: Throwable) {
                regions.value = null
            }

            override fun onResponse(
                call: Call<RegionsApiResult>,
                response: Response<RegionsApiResult>
            ) {
                regions.value = response.body()
            }

        })
        return regions
    }

    fun createNewMansion(token: String, newMansion: NewMansion): MutableLiveData<Mansion> {
        val mansion = MutableLiveData<Mansion>()
        userApi.createNewMansion("Token $token", newMansion).enqueue(object : Callback<Mansion> {
            override fun onFailure(call: Call<Mansion>, t: Throwable) {
                mansion.value = null
            }

            override fun onResponse(call: Call<Mansion>, response: Response<Mansion>) {
                mansion.value = response.body()
            }

        })
        return mansion
    }

    fun updateMansion(token: String, id: Int, newMansion: NewMansion): MutableLiveData<Mansion> {
        val mansion = MutableLiveData<Mansion>()
        userApi.updateMansion("Token $token", id, newMansion).enqueue(object : Callback<Mansion> {
            override fun onFailure(call: Call<Mansion>, t: Throwable) {
                mansion.value = null
            }

            override fun onResponse(call: Call<Mansion>, response: Response<Mansion>) {
                mansion.value = response.body()
            }

        })
        return mansion
    }

    fun createFacilities(
        token: String,
        newFacilities: NewFacilities
    ): MutableLiveData<NewFacilities> {

        val facilities = MutableLiveData<NewFacilities>()

        userApi.createFacilities("Token $token", newFacilities)
            .enqueue(object : Callback<NewFacilities> {
                override fun onFailure(call: Call<NewFacilities>, t: Throwable) {
                    facilities.value = null
                }

                override fun onResponse(
                    call: Call<NewFacilities>,
                    response: Response<NewFacilities>
                ) {
                    facilities.value = response.body()
                }

            })

        return facilities
    }

    fun deleteFacility(token: String, id: Int): MutableLiveData<String> {
        val message = MutableLiveData<String>()
        userApi.deleteFacility("Token $token", id).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                message.value = null
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                message.value = response.message()
            }

        })

        return message
    }

    fun uploadMansionPicture(
        token: String,
        mansionId: RequestBody,
        picture: MultipartBody.Part
    ): MutableLiveData<PictureResult> {
        val pictureData = MutableLiveData<PictureResult>()

        userApi.uploadMansionPicture("Token $token", picture, mansionId)
            .enqueue(object : Callback<PictureResult> {
                override fun onFailure(call: Call<PictureResult>, t: Throwable) {
                    pictureData.value = null
                }

                override fun onResponse(
                    call: Call<PictureResult>,
                    response: Response<PictureResult>
                ) {
                    if (response.isSuccessful)
                        Log.d("Response", response.message())
                    else
                        Log.d("Response", response.message())
                    pictureData.value = response.body()
                }

            })
        return pictureData
    }

    fun filterMansionsListByRegionAndRooms(
        token: String,
        regionNum: Int,
        roomsNum: Int
    ): MutableLiveData<MansionsResult> {
        val mansions = MutableLiveData<MansionsResult>()


        userApi.filterMansionListByRegionAndRoom("Token $token", regionNum)
            .enqueue(object : Callback<MansionsResult> {
                override fun onFailure(call: Call<MansionsResult>, t: Throwable) {
                    mansions.value = null
                }

                override fun onResponse(
                    call: Call<MansionsResult>,
                    response: Response<MansionsResult>
                ) {
                    mansions.value = response.body()
                }

            })

        return mansions
    }

    fun bookMansion(token: String, bookDetails: BookDetails): MutableLiveData<BookDetailsResult> {
        val result = MutableLiveData<BookDetailsResult>()
        userApi.bookMansion("Token $token", bookDetails)
            .enqueue(object : Callback<BookDetailsResult> {
                override fun onFailure(call: Call<BookDetailsResult>, t: Throwable) {
                    result.value = null
                }

                override fun onResponse(
                    call: Call<BookDetailsResult>,
                    response: Response<BookDetailsResult>
                ) {
                    result.value = response.body()
                }

            })

        return result
    }

    fun rateMansionById(
        token: String,
        rating: Int,
        mansionId: Int
    ): MutableLiveData<RatingMansionResult> {
        val result = MutableLiveData<RatingMansionResult>()

        userApi.rateMansionById("Token $token", rating, mansionId)
            .enqueue(object : Callback<RatingMansionResult> {
                override fun onFailure(call: Call<RatingMansionResult>, t: Throwable) {
                    result.value = null
                }

                override fun onResponse(
                    call: Call<RatingMansionResult>,
                    response: Response<RatingMansionResult>
                ) {
                    result.value = response.body()
                }

            })

        return result
    }

}