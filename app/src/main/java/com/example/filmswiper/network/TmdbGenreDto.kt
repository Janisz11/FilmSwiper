package com.example.filmswiper.network

import com.google.gson.annotations.SerializedName

data class TmdbGenreResponse(
    @SerializedName("genres") val genres: List<TmdbGenreDto>
)

data class TmdbGenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
