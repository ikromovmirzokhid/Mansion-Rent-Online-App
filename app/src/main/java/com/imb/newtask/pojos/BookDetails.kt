package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class BookDetails(
    @SerializedName("start") val startDate: String, @SerializedName("end") val endDate: String,
    @SerializedName("is_cancelable") val isCancelable: Boolean, @SerializedName("mansion") val mansionId: Int
)