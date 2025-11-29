package com.example.filmswiper.data

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val year: Int?,
    val genres: List<String>,
    val rating: Double?,
    val status: MovieStatus,
    val userRating: Int?,
    val posterUrl: String? // 👈 NOWE
)
