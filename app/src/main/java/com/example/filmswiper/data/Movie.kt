package com.example.filmswiper.data

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val year: Int?,
    val genres: List<String>,
    val rating: Double?,           // np. ocena z TMDB/IMDB
    val status: MovieStatus = MovieStatus.NEW,
    val userRating: Int? = null    // Twoja ocena (1–10)
)
