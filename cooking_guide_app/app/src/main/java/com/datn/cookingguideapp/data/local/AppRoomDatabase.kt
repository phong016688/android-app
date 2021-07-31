package com.datn.cookingguideapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.datn.cookingguideapp.R
import com.datn.cookingguideapp.domain.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    context.getString(R.string.app_name) + "_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}