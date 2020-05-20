package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class MansionFacility(@SerializedName("id") val id: Int, @SerializedName("facility") val name: String)