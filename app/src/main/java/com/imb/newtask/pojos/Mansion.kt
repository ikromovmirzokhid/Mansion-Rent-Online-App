package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class Mansion(
    @SerializedName("id") val id: Int, @SerializedName("title") val title: String,
    @SerializedName("address") val address: String, @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String, @SerializedName("available") val isAvailable: Boolean,
    @SerializedName("is_blocked") val isBlocked: Boolean, @SerializedName("rooms") val numOfRooms: Int,
    @SerializedName("price") val price: String, @SerializedName("owner") val owner: Owner,
    @SerializedName("region") val region: Region, @SerializedName("pictures") val images: List<Picture>,
    @SerializedName("facilities") val facilities: List<MansionFacility>, @SerializedName("rates") val rates: Rates,
    @SerializedName("created_date") val createdDate: String, @SerializedName("modified_date") val modifiedDate: String
)