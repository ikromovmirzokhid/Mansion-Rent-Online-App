package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int, @SerializedName("username") val username: String,
    @SerializedName("first_name") val firstName: String, @SerializedName("last_name") val lastName: String,
    @SerializedName("date_of_birth") val dob: String, @SerializedName("phone") val phone: String,
    @SerializedName("staff") val isStaff: Boolean, @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("is_owner") val isOwner: Boolean
)
