package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class PictureResult(
    @SerializedName("id") val id: Int, @SerializedName("picture") val picture: String,
    @SerializedName("height_field") val height: Int, @SerializedName("width_field") val width: Int,
    @SerializedName("created_date") val date: String, @SerializedName("mansion") val mansionId: Int
)