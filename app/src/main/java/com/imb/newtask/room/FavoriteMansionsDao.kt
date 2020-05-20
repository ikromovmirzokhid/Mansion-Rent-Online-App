package com.imb.newtask.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteMansionsDao {

    @Query("select * from favorite_mansions")
    fun getAllFavoriteMansions(): LiveData<List<FavoriteMansion>>

    @Query("select * from favorite_mansions where mansionId= :id")
    fun getMansionById(id: Int): LiveData<FavoriteMansion>

    @Insert
    fun insertNewFavouriteMansion(mansion: FavoriteMansion)

    @Delete
    fun deleteMansionFromFavoriteDB(mansion: FavoriteMansion): Int

}