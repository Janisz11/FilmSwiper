// FILE: DatabaseModule.kt (Dependency Injection)
package com.example.filmswiper.database

import android.content.Context
import com.example.filmswiper.database.dao.MovieDao

object DatabaseModule {

    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        database = AppDatabase.getInstance(context)
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException(
            "Database not initialized. Call DatabaseModule.initialize(context) first"
        )
    }

    fun getMovieDao(): MovieDao {
        return getDatabase().movieDao()
    }
}