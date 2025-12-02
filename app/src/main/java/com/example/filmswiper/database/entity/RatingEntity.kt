// FILE: RatingEntity.kt
package com.example.filmswiper.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RatingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: Long,
    val userRating: Int,  // 1-10
    val ratedAt: Long = System.currentTimeMillis(),
    val source: String = "local"  // "local", "imdb", "letterboxd"
)

// ============================================================