package com.imb.newtask.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecentSearchedMansionsDao {

    @Query("select * from recent_searched_mansions")
    fun getAllRecentSearchedMansions(): LiveData<List<RecentSearchedMansion>>

    @Query("select * from recent_searched_mansions where id= :id")
    fun getRecentSearchedMansionById(id: Int): LiveData<RecentSearchedMansion>

    @Insert
    fun insertRecentSearchedMansion(mansion: RecentSearchedMansion)

    @Update
    fun updateRecentSearchedMansion(mansion: RecentSearchedMansion): Int
}