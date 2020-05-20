package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class Picture(@SerializedName("id") val id: Int, @SerializedName("picture") val picture: String)