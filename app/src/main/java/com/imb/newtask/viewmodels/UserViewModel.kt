package com.imb.newtask.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imb.newtask.pojos.UserLogin
import com.imb.newtask.pojos.UserRegister
import com.imb.newtask.repository.UserRepository

class UserViewModel : ViewModel() {
    lateinit var loginUserData: MutableLiveData<UserLogin>
    lateinit var registeredUserData: MutableLiveData<UserRegister>

    fun login(username: String, password: String): LiveData<UserLogin> {
        loginUserData = UserRepository.login(username, password)
        return loginUserData
    }

    fun register(user: UserRegister): LiveData<UserRegister> {
        registeredUserData = UserRepository.register(user)
        return registeredUserData
    }

}