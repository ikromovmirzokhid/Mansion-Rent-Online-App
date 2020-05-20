package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class BookDetailsResult(
    @SerializedName("id") val id: Int, @SerializedName("star") val startDate: String,
    @SerializedName("end") val endDate: String, @SerializedName("is_cancelable") val isCancelable: Boolean,
    @SerializedName("is_active") val isActive: Boolean, @SerializedName("total_price") val totalPrice: String,
    @SerializedName("mansion") val mansionId: Int, @SerializedName("user") val user: User,
    @SerializedName("created_date") val createdDate: String
)