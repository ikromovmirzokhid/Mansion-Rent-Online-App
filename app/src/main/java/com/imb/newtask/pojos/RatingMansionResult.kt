package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class RatingMansionResult(
    @SerializedName("id") val id: Int, @SerializedName("rate") val rating: Int,
    @SerializedName("created_date") val createdDate: String, @SerializedName("mansion") val mansion: Int
)