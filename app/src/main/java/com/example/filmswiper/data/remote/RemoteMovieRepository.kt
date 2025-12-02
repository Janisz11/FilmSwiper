package com.example.filmswiper.data.remote

import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieFilter
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.network.MovieApiService
import com.example.filmswiper.network.TmdbGenreResponse
import com.example.filmswiper.network.TmdbMovieDto
import kotlin.random.Random

class RemoteMovieRepository(
    private val api: MovieApiService,
    private val apiKey: String
) {
    private var genreMap: Map<Int, String>? = null
    private val imageBaseUrl = "https://image.tmdb.org/t/p/w500"
    private val random = Random(System.currentTimeMillis())

    // ✨ NOWE: Filtruj filmy według podanych kryteriów
    suspend fun getMoviesByFilter(filter: MovieFilter, count: Int = 20): List<Movie> {
        if (genreMap == null) {
            loadGenres()
        }

        return try {
            when {
                // Jeśli jest tytuł - szukaj po tytule
                filter.title.isNotEmpty() -> {
                    searchByTitle(filter.title, count)
                }
                // Jeśli są gatunki lub rok - użyj discover
                filter.genreIds.isNotEmpty() || filter.yearFrom != null || filter.yearTo != null -> {
                    searchByFilters(filter, count)
                }
                // Jeśli pusty filter - losowe filmy
                else -> getMoviesMixed(count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ✨ Szukanie po tytule
    private suspend fun searchByTitle(title: String, count: Int): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val pagesToFetch = (count / 20) + 1

        repeat(pagesToFetch) { page ->
            try {
                val response = api.searchMovieByTitle(
                    apiKey = apiKey,
                    title = title,
                    page = page + 1
                )
                val movies = response.results.mapNotNull { it.toDomain() }
                allMovies.addAll(movies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return allMovies.take(count)
    }

    // ✨ Szukanie po gatunkach i roku
    private suspend fun searchByFilters(filter: MovieFilter, count: Int): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val pagesToFetch = (count / 20) + 1

        repeat(pagesToFetch) { pageIndex ->
            try {
                // Formatuj genreIds jako "28,35,18" dla API
                val genreString = if (filter.genreIds.isNotEmpty()) {
                    filter.genreIds.joinToString(",")
                } else {
                    null
                }

                val response = api.searchMovies(
                    apiKey = apiKey,
                    genreIds = genreString,
                    year = filter.yearFrom,  // TMDb szuka po primary_release_year
                    page = pageIndex + 1
                )
                val movies = response.results.mapNotNull { it.toDomain() }
                allMovies.addAll(movies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // ✨ Filtruj lokalnie po roku (od - do)
        return allMovies
            .filter { movie ->
                if (filter.yearTo != null && movie.year != null) {
                    movie.year in filter.yearFrom!! .. filter.yearTo
                } else {
                    true
                }
            }
            .take(count)
    }

    suspend fun getRandomMovies(count: Int = 20): List<Movie> {
        if (genreMap == null) {
            loadGenres()
        }

        val allMovies = mutableListOf<Movie>()
        val pagesToFetch = (count / 20) + 1

        repeat(pagesToFetch) {
            val randomPage = random.nextInt(1, 501)
            try {
                val response = api.getPopularMovies(apiKey = apiKey, page = randomPage)
                val movies = response.results.mapNotNull { it.toDomain() }
                allMovies.addAll(movies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return allMovies.shuffled().take(count)
    }

    suspend fun getRandomMovie(): Movie? {
        if (genreMap == null) {
            loadGenres()
        }

        return try {
            val randomPage = random.nextInt(1, 501)
            val response = api.getPopularMovies(apiKey = apiKey, page = randomPage)

            response.results
                .mapNotNull { it.toDomain() }
                .randomOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMoviesByGenre(genreId: Int, count: Int = 20): List<Movie> {
        if (genreMap == null) {
            loadGenres()
        }

        val allMovies = mutableListOf<Movie>()
        val pagesToFetch = (count / 20) + 1

        repeat(pagesToFetch) {
            try {
                val randomPage = random.nextInt(1, 501)
                val response = api.getMoviesByGenre(
                    apiKey = apiKey,
                    genreId = genreId,
                    page = randomPage
                )
                val movies = response.results.mapNotNull { it.toDomain() }
                allMovies.addAll(movies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return allMovies.shuffled().take(count)
    }

    suspend fun getMoviesMixed(count: Int = 20): List<Movie> {
        if (genreMap == null) {
            loadGenres()
        }

        val allMovies = mutableListOf<Movie>()
        val pagesToFetch = (count / 20) + 1

        repeat(pagesToFetch) {
            try {
                val randomPage = random.nextInt(1, 501)

                val response = if (random.nextBoolean()) {
                    api.getPopularMovies(apiKey = apiKey, page = randomPage)
                } else {
                    api.getTopRatedMovies(apiKey = apiKey, page = randomPage)
                }

                val movies = response.results.mapNotNull { it.toDomain() }
                allMovies.addAll(movies)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return allMovies.shuffled().take(count)
    }

    suspend fun getMovies(): List<Movie> {
        return getRandomMovies(20)
    }

    // ✨ NOWE: Pobierz mapę gatunków
    suspend fun getGenres(): Map<Int, String> {
        if (genreMap == null) {
            loadGenres()
        }
        return genreMap ?: emptyMap()
    }

    private suspend fun loadGenres() {
        try {
            val response: TmdbGenreResponse = api.getMovieGenres(apiKey = apiKey)
            genreMap = response.genres.associate { it.id to it.name }
        } catch (e: Exception) {
            e.printStackTrace()
            genreMap = emptyMap()
        }
    }

    private fun TmdbMovieDto.toDomain(): Movie? {
        val safeTitle = title ?: return null

        val genresNames: List<String> = genreIds
            ?.mapNotNull { id -> genreMap?.get(id) }
            ?.ifEmpty { null }
            ?: emptyList()

        val fullPosterUrl = posterPath?.let { path ->
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