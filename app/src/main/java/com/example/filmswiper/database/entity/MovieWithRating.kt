// FILE: MovieWithRating.kt
package com.example.filmswiper.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovieWithRating(
    @Embedded
    val movie: MovieEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "movieId"
    )
    val ratings: List<RatingEntity> = emptyList()
)
