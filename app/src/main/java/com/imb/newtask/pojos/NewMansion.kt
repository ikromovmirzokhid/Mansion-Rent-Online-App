package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class NewMansion(
    @SerializedName("title") val title: String,
    @SerializedName("address") val address: String, @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String, @SerializedName("rooms") val numOfRooms: Int,
    @SerializedName("price") val price: String, @SerializedName("region") val region: Int
)