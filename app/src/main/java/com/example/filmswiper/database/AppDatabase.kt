// FILE: AppDatabase.kt
package com.example.filmswiper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.filmswiper.database.dao.MovieDao
import com.example.filmswiper.database.entity.MovieEntity
import com.example.filmswiper.database.entity.RatingEntity

@Database(
    entities = [
        MovieEntity::class,
        RatingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "filmswiper_database"
                )
                    .fallbackToDestructiveMigration()  // For development
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}


