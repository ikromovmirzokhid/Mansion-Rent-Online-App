package com.imb.newtask.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.imb.newtask.pojos.Facilities
import com.imb.newtask.pojos.Owner
import com.imb.newtask.pojos.Rates
import com.imb.newtask.pojos.Region

@Entity(tableName = "favorite_mansions")
data class FavoriteMansion(
    @ColumnInfo(name = "mansionId")
    var mansionId: Int,
    var title: String,
    var address: String,
    var latitude: String,
    var longitude: String,
    var isAvailable: Boolean,
    var isBlocked: Boolean,
    var numOfRooms: Int,
    var price: String, @Embedded(prefix = "owner") var owner: Owner,
    @Embedded(prefix = "region") val region: Region,
    var images: String, @Embedded(prefix = "facilities") var facilities: Facilities,
    @Embedded(prefix = "rating") var rates: Rates,
    var createdDate: String,
    var modifiedDate: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}