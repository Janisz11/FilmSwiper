// FILE: MovieEntity.kt
package com.example.filmswiper.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val overview: String,
    val year: Int?,
    val genres: String,  // JSON string: "Action,Horror,Comedy"
    val rating: Double?,
    val posterUrl: String?,
    val status: String,  // NEW, WATCHED_RATED, BLACKLISTED
    val lastUpdated: Long = System.currentTimeMillis()
)