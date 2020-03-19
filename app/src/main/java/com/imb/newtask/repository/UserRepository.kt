package com.imb.newtask.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.imb.newtask.api.UsersAPi
import com.imb.newtask.pojos.UserLogin
import com.imb.newtask.pojos.UserRegister
import com.imb.newtask.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRepository {

    private var userApi: UsersAPi

    init {
        var loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
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
                Log.d("Error", "Null")
            }

            override fun onResponse(call: Call<UserRegister>, response: Response<UserRegister>) {
                userData.value = response.body()
                Log.d("Success", response.message())
            }
        })

        return userData
    }

}