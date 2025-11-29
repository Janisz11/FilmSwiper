package com.example.filmswiper.data.remote

import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.network.MovieApiService
import com.example.filmswiper.network.TmdbGenreResponse
import com.example.filmswiper.network.TmdbMovieDto

class RemoteMovieRepository(
    private val api: MovieApiService,
    private val apiKey: String
) {

    private var cache: List<Movie>? = null
    private var genreMap: Map<Int, String>? = null

    private val imageBaseUrl = "https://image.tmdb.org/t/p/w500" // 👈 stała do plakatów

    suspend fun getMovies(): List<Movie> {
        if (cache != null) return cache!!

        // 1. Upewniamy się, że mamy mapę gatunków
        if (genreMap == null) {
            loadGenres()
        }

        // 2. Pobieramy popularne filmy
        val response = api.getPopularMovies(apiKey = apiKey)
        val movies = response.results.mapNotNull { it.toDomain() }

        cache = movies
        return movies
    }

    private suspend fun loadGenres() {
        val response: TmdbGenreResponse = api.getMovieGenres(apiKey = apiKey)
        genreMap = response.genres.associate { it.id to it.name }
    }

    private fun TmdbMovieDto.toDomain(): Movie? {
        val safeTitle = title ?: return null

        val genresNames: List<String> = genreIds
            ?.mapNotNull { id -> genreMap?.get(id) }
            ?.ifEmpty { null }
            ?: emptyList()

        val fullPosterUrl = posterPath?.let { path ->
            // TMDb zwraca np. "/abcd1234.jpg"
            "$imageBaseUrl$path"
        }

        return Movie(
            id = id,
            title = safeTitle,
            overview = overview ?: "",
            year = releaseDate?.take(4)?.toIntOrNull(),
            genres = genresNames,
            rating = voteAverage,
            status = MovieStatus.NEW,
            userRating = null,
            posterUrl = fullPosterUrl
        )
    }
}
