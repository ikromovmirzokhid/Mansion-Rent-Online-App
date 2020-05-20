package com.imb.newtask.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMansion::class, RecentSearchedMansion::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun favoriteMansionsDao(): FavoriteMansionsDao
    abstract fun recentSearchedMansionsDao(): RecentSearchedMansionsDao

    companion object {
        @Volatile
        private var INSTANCE: com.imb.newtask.room.Database? = null

        fun init(context: Context){
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    com.imb.newtask.room.Database::class.java,
                    "mansionsDB"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
        }

        fun getDatabase(): com.imb.newtask.room.Database {

            return INSTANCE!!
        }
    }
}