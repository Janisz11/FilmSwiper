package com.example.filmswiper.network

import com.google.gson.annotations.SerializedName

data class TmdbMovieResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TmdbMovieDto>
)

data class TmdbMovieDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    @SerializedName("poster_path") val posterPath: String?
)
