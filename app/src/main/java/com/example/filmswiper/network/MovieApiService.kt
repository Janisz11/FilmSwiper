package com.example.filmswiper.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    // Popularne filmy
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbMovieResponse

    // Top rated filmy
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbMovieResponse

    // Filmy z konkretnego gatunku
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TmdbMovieResponse

    // Discover z filtrami (gatunki, rok, etc.)
    @GET("discover/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("with_genres") genreIds: String? = null,
        @Query("primary_release_year") year: Int? = null,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TmdbMovieResponse

    // Szukaj po tytule
    @GET("search/movie")
    suspend fun searchMovieByTitle(
        @Query("api_key") apiKey: String,
        @Query("query") title: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TmdbMovieResponse

    // Lista gatunków
    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): TmdbGenreResponse
}