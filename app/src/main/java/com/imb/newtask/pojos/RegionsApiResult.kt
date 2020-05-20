package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class RegionsApiResult(
    @SerializedName("count") val count: Int, @SerializedName("next") val next: String,
    @SerializedName("previous") val previous: String, @SerializedName("results") val regions: List<Region>
)