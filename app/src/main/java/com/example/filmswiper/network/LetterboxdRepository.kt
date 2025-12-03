// FILE: data/remote/LetterboxdRepository.kt
package com.example.filmswiper.data.remote

import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.database.MovieMapper
import com.example.filmswiper.database.dao.MovieDao
import com.example.filmswiper.database.entity.MovieEntity
import com.example.filmswiper.network.LetterboxdImportRequest
import com.example.filmswiper.network.LetterboxdMovieRating
import com.example.filmswiper.network.MovieApiService
import com.example.filmswiper.network.LetterboxdApiService

data class ImportResult(
    val success: Boolean,
    val importedCount: Int,
    val skippedCount: Int,
    val message: String
)

class LetterboxdRepository(
    private val letterboxdApi: LetterboxdApiService,
    private val tmdbApi: MovieApiService,
    private val movieDao: MovieDao,
    private val tmdbApiKey: String
) {

    suspend fun importFromLetterboxd(
        username: String,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): ImportResult {
        return try {
            // 1. Fetch ratings from Python backend
            val response = letterboxdApi.importFromLetterboxd(
                LetterboxdImportRequest(username)
            )

            if (!response.success || response.movies.isEmpty()) {
                return ImportResult(
                    success = false,
                    importedCount = 0,
                    skippedCount = 0,
                    message = "No movies found for user: $username"
                )
            }

            var importedCount = 0
            var skippedCount = 0
            val total = response.movies.size

            // 2. For each movie, search TMDB and save to database
            response.movies.forEachIndexed { index, letterboxdMovie ->
                onProgress(index + 1, total)

                val saved = matchAndSaveMovie(letterboxdMovie)
                if (saved) {
                    importedCount++
                } else {
                    skippedCount++
                }
            }

            ImportResult(
                success = true,
                importedCount = importedCount,
                skippedCount = skippedCount,
                message = "Zaimportowano $importedCount filmów z Letterboxd"
            )

        } catch (e: Exception) {
            e.printStackTrace()
            ImportResult(
                success = false,
                importedCount = 0,
                skippedCount = 0,
                message = "Błąd: ${e.message}"
            )
        }
    }

    private suspend fun matchAndSaveMovie(letterboxdMovie: LetterboxdMovieRating): Boolean {
        try {
            // Parse title and year from "Inside Out 2 (2024)"
            val (title, year) = parseTitle(letterboxdMovie.title)

            // Search TMDB for this movie
            val searchResponse = tmdbApi.searchMovieByTitle(
                apiKey = tmdbApiKey,
                title = title,
                page = 1
            )

            if (searchResponse.results.isEmpty()) {
                return false
            }

            // Find best match (same title and year if possible)
            val tmdbMovie = searchResponse.results.firstOrNull { result ->
                val resultYear = result.releaseDate?.take(4)?.toIntOrNull()
                if (year != null && resultYear != null) {
                    resultYear == year
                } else {
                    true
                }
            } ?: searchResponse.results.first()

            // Check if movie already exists
            val existingMovie = movieDao.getMovieById(tmdbMovie.id)

            if (existingMovie != null) {
                // Movie exists - check if already has letterboxd rating
                val existingRatings = movieDao.getRatingsForMovie(tmdbMovie.id)
                val hasLetterboxdRating = existingRatings.any { it.source == "letterboxd" }

                if (!hasLetterboxdRating) {
                    // Add letterboxd rating
                    val ratingEntity = MovieMapper.createRatingEntity(
                        movieId = tmdbMovie.id,
                        userRating = letterboxdMovie.rating,
                        source = "letterboxd"
                    )
                    movieDao.insertRating(ratingEntity)
                }
            } else {
                // New movie - save movie and rating
                val movieEntity = MovieEntity(
                    id = tmdbMovie.id,
                    title = tmdbMovie.title ?: title,
                    overview = tmdbMovie.overview ?: "",
                    year = tmdbMovie.releaseDate?.take(4)?.toIntOrNull(),
                    genres = tmdbMovie.genreIds?.joinToString(",") ?: "",
                    rating = tmdbMovie.voteAverage,
                    posterUrl = tmdbMovie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
                    status = MovieStatus.WATCHED_RATED.name
                )

                val ratingEntity = MovieMapper.createRatingEntity(
                    movieId = tmdbMovie.id,
                    userRating = letterboxdMovie.rating,
                    source = "letterboxd"
                )

                movieDao.insertMovie(movieEntity)
                movieDao.insertRating(ratingEntity)
            }

            return true

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // Parse "Inside Out 2 (2024)" -> Pair("Inside Out 2", 2024)
    private fun parseTitle(fullTitle: String): Pair<String, Int?> {
        val regex = """^(.+?)\s*\((\d{4})\)$""".toRegex()
        val match = regex.find(fullTitle)

        return if (match != null) {
            val title = match.groupValues[1].trim()
            val year = match.groupValues[2].toIntOrNull()
            Pair(title, year)
        } else {
            Pair(fullTitle, null)
        }
    }
}