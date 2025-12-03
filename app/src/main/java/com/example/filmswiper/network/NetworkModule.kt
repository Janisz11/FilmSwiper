// FILE: network/NetworkModule.kt (UPDATED)
package com.example.filmswiper.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

    // For emulator: "http://10.0.2.2:8000"
    // For physical device: use your PC's IP like "http://192.168.1.XXX:8000"
    private const val LETTERBOXD_BASE_URL = "http://10.0.2.2:8000"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Longer timeout for Letterboxd (scraping takes time)
    private val letterboxdOkHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)  // 2 minutes for scraping
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val tmdbRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val letterboxdRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(LETTERBOXD_BASE_URL)
        .client(letterboxdOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val movieApi: MovieApiService = tmdbRetrofit.create(MovieApiService::class.java)

    val letterboxdApi: LetterboxdApiService = letterboxdRetrofit.create(LetterboxdApiService::class.java)
}