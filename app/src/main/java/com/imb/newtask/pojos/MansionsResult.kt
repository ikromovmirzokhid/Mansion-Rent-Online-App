package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class MansionsResult(
    @SerializedName("count") val count: Int, @SerializedName("next") val next: String,
    @SerializedName("previous") val previous: String, @SerializedName("results") val mansions: ArrayList<Mansion>
)