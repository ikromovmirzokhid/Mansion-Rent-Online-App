package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class UserRegister(
    @SerializedName("username") val username: String, @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String, @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("phone") val phoneNum: String, @SerializedName("is_owner") val isOwner: Boolean,
    @SerializedName("password") val password: String
)