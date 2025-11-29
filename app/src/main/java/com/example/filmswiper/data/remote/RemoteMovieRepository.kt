package com.example.filmswiper.data.remote

import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.network.MovieApiService
import com.example.filmswiper.network.TmdbMovieDto

class RemoteMovieRepository(
    private val api: MovieApiService,
    private val apiKey: String
) {

    private var cache: List<Movie>? = null

    suspend fun getMovies(): List<Movie> {
        if (cache != null) return cache!!

        val response = api.getPopularMovies(apiKey = apiKey)
        val movies = response.results.mapNotNull { it.toDomain() }

        cache = movies
        return movies
    }

    private fun TmdbMovieDto.toDomain(): Movie? {
        val safeTitle = title ?: return null

        return Movie(
            id = id,
            title = safeTitle,
            overview = overview ?: "",
            year = releaseDate?.take(4)?.toIntOrNull(),
            genres = emptyList(), // później można zmapować genre_ids -> nazwy
            rating = voteAverage,
            status = MovieStatus.NEW,
            userRating = null
        )
    }
}
