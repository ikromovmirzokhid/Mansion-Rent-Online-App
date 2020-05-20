package com.imb.newtask.pojos

import com.google.gson.annotations.SerializedName

data class NewFacilities(@SerializedName("mansion") val mansionId: Int, @SerializedName("facilities") val facilities: List<Facility>)