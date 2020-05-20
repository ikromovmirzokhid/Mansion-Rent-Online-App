package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class Region(@SerializedName("id") val id: Int, @SerializedName("title") val title: String)