// FILE: network/LetterboxdApiService.kt
package com.example.filmswiper.network

import retrofit2.http.Body
import retrofit2.http.POST

data class LetterboxdImportRequest(
    val username: String
)

data class LetterboxdMovieRating(
    val title: String,
    val rating: Int
)

data class LetterboxdImportResponse(
    val success: Boolean,
    val movies: List<LetterboxdMovieRating>,
    val count: Int
)

interface LetterboxdApiService {
    @POST("/api/import-letterboxd")
    suspend fun importFromLetterboxd(
        @Body request: LetterboxdImportRequest
    ): LetterboxdImportResponse
}