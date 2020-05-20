package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class Rates(
    @SerializedName("max_rate") val maxRate: Int, @SerializedName("avg_rate") val avgRate: Float,
    @SerializedName("min_rate") val minRate: Int, @SerializedName("all_rates") val allRates: Int
)