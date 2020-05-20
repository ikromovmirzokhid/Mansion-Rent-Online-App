package com.imb.newtask.utils

import com.imb.newtask.pojos.Facilities
import com.imb.newtask.pojos.Mansion
import com.imb.newtask.room.FavoriteMansion
import com.imb.newtask.room.RecentSearchedMansion

class Converter {
    companion object {
        private var hasTv = false
        private var hasPlay = false
        private var hasPool = false
        private var hasSwim = false

        fun convertToRecentSearchedMansion(m: Mansion): RecentSearchedMansion {
            hasTv = false
            hasPlay = false
            hasPool = false
            hasSwim = false

            val facilitiesList = m.facilities
            var picture = ""
            if (m.images != null)
                picture = m.images[0].picture

            if (facilitiesList != null) {

                for (i in 0 until facilitiesList.size) {
                    if (facilitiesList[i].name.equals("TV"))
                        hasTv = true
                    if (facilitiesList[i].name.equals("Pool"))
                        hasPool = true
                    if (facilitiesList[i].name.equals("Swimming Pool"))
                        hasSwim = true
                    if (facilitiesList[i].name.equals("Playstation"))
                        hasPlay = true
                }
            }
            val facilities = Facilities(hasTv, hasSwim, hasPool, hasPlay)
            val mansion = RecentSearchedMansion(
                m.id,
                m.title,
                m.address,
                m.latitude,
                m.longitude,
                m.isAvailable,
                m.isBlocked,
                m.numOfRooms,
                m.price,
                m.owner,
                m.region,
                picture,
                facilities,
                m.rates,
                m.createdDate,
                m.modifiedDate
            )

            return mansion
        }


        fun convertToRecentSearchedMansion(m: FavoriteMansion): RecentSearchedMansion {
            val mansion = RecentSearchedMansion(
                m.mansionId,
                m.title,
                m.address,
                m.latitude,
                m.longitude,
                m.isAvailable,
                m.isBlocked,
                m.numOfRooms,
                m.price,
                m.owner,
                m.region,
                m.images,
                m.facilities,
                m.rates,
                m.createdDate,
                m.modifiedDate
            )

            return mansion
        }


        fun convertToFavoriteMansion(m: RecentSearchedMansion): FavoriteMansion {
            val mansion = FavoriteMansion(
                m.mansionId,
                m.title,
                m.address,
                m.latitude,
                m.longitude,
                m.isAvailable,
                m.isBlocked,
                m.numOfRooms,
                m.price,
                m.owner,
                m.region,
                m.image,
                m.facilities,
                m.rates,
                m.createdDate,
                m.modifiedDate
            )

            return mansion
        }
    }
}