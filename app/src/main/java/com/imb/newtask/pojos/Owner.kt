package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("id") val id: Int, @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String, @SerializedName("is_owner") val isOwner: Boolean,
    @SerializedName("phone") val phone: String
)