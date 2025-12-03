// FILE: SwipeViewModel.kt
package com.example.filmswiper.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieFilter
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.data.remote.ImportResult
import com.example.filmswiper.data.remote.LetterboxdRepository
import com.example.filmswiper.data.remote.RemoteMovieRepository
import com.example.filmswiper.database.DatabaseModule
import com.example.filmswiper.database.MovieMapper
import com.example.filmswiper.database.dao.MovieDao
import com.example.filmswiper.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SwipeUiState {
    object Loading : SwipeUiState()
    object Empty : SwipeUiState()
    data class Success(val movie: Movie) : SwipeUiState()
    data class Error(val message: String) : SwipeUiState()
}

class SwipeViewModel : ViewModel() {

    private val apiKey = "8d0ba6c554df6264bc090e7cec044811"

    private val remoteRepository = RemoteMovieRepository(
        api = NetworkModule.movieApi,
        apiKey = apiKey
    )

    private val movieDao: MovieDao = DatabaseModule.getMovieDao()

    private val sessionLiked = mutableSetOf<Long>()
    private val sessionDisliked = mutableSetOf<Long>()

    private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _genres = MutableStateFlow<Map<Int, String>>(emptyMap())
    val genres: StateFlow<Map<Int, String>> = _genres.asStateFlow()

    // Czytaj obejrzane filmy z Room (Flow - auto-updates)
    val watchedMovies = movieDao.getWatchedMoviesFlow()

    private val movieHistory = mutableListOf<Movie>()

    // ===== LETTERBOXD IMPORT =====

    sealed class LetterboxdState {
        object Idle : LetterboxdState()
        data class Loading(val current: Int = 0, val total: Int = 0) : LetterboxdState()
        data class Success(val result: ImportResult) : LetterboxdState()
        data class Error(val message: String) : LetterboxdState()
    }

    private val _letterboxdState = MutableStateFlow<LetterboxdState>(LetterboxdState.Idle)
    val letterboxdState: StateFlow<LetterboxdState> = _letterboxdState.asStateFlow()

    private val letterboxdRepository = LetterboxdRepository(
        letterboxdApi = NetworkModule.letterboxdApi,
        tmdbApi = NetworkModule.movieApi,
        movieDao = movieDao,
        tmdbApiKey = apiKey
    )

    // ===== INIT =====

    init {
        loadMovies()
        loadGenres()
    }

    // ===== MOVIE LOADING =====

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            try {
                val movies = remoteRepository.getMoviesMixed(count = 50)
                _allMovies.value = movies

                val movieEntities = movies.map { MovieMapper.toEntity(it) }
                movieDao.insertMovies(movieEntities)

                loadNextMovie()
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(
                    e.message ?: "Błąd pobierania filmów"
                )
            }
        }
    }

    private fun loadNextMovie() {
        viewModelScope.launch {
            val all = _allMovies.value
            if (all.isEmpty()) {
                _uiState.value = SwipeUiState.Empty
                return@launch
            }

            val excludeIds = sessionLiked + sessionDisliked

            val candidates = all.filter { movie ->
                movie.status != MovieStatus.WATCHED_RATED &&
                        movie.status != MovieStatus.BLACKLISTED &&
                        movie.id !in excludeIds
            }

            val nextMovie = candidates.randomOrNull()
            _uiState.value = if (nextMovie != null) {
                SwipeUiState.Success(nextMovie)
            } else {
                SwipeUiState.Empty
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                val genresMap = remoteRepository.getGenres()
                _genres.value = genresMap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ===== SWIPE ACTIONS =====

    fun onSwipeRight() {
        _uiState.value.let { state ->
            if (state is SwipeUiState.Success) {
                sessionLiked += state.movie.id
                movieHistory.add(state.movie)
            }
        }
        loadNextMovie()
    }

    fun onSwipeLeft() {
        _uiState.value.let { state ->
            if (state is SwipeUiState.Success) {
                sessionDisliked += state.movie.id
                movieHistory.add(state.movie)
            }
        }
        loadNextMovie()
    }

    fun onBlacklist() {
        _uiState.value.let { state ->
            if (state is SwipeUiState.Success) {
                sessionDisliked += state.movie.id
                movieHistory.add(state.movie)
            }
        }
        loadNextMovie()
    }

    fun onMarkAsWatched(rating: Int) {
        println("📝 onMarkAsWatched() called with rating: $rating")
        _uiState.value.let { state ->
            if (state is SwipeUiState.Success) {
                val movie = state.movie
                sessionLiked += movie.id
                movieHistory.add(movie)

                viewModelScope.launch {
                    try {
                        val movieEntity = MovieMapper.toEntity(movie)
                        movieDao.insertMovie(movieEntity)

                        val ratingEntity = MovieMapper.createRatingEntity(
                            movieId = movie.id,
                            userRating = rating,
                            source = "local"
                        )
                        movieDao.insertRating(ratingEntity)

                        println("✅ Film obejrzany: ${movie.title} | Ocena: $rating")
                        println("💾 Zapisano do Room Database")
                    } catch (e: Exception) {
                        println("❌ Błąd przy zapisie: ${e.message}")
                        e.printStackTrace()
                    }
                }
            } else {
                println("❌ uiState nie jest Success: $state")
            }
        }
        loadNextMovie()
    }

    fun undoLastAction() {
        if (movieHistory.isNotEmpty()) {
            val lastMovie = movieHistory.removeAt(movieHistory.size - 1)
            sessionLiked.remove(lastMovie.id)
            sessionDisliked.remove(lastMovie.id)
            _uiState.value = SwipeUiState.Success(lastMovie)
        }
    }

    // ===== FILTER =====

    fun applyFilter(filter: MovieFilter) {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            try {
                val movies = remoteRepository.getMoviesByFilter(filter, count = 50)
                _allMovies.value = movies

                val movieEntities = movies.map { MovieMapper.toEntity(it) }
                movieDao.insertMovies(movieEntities)

                loadNextMovie()
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(
                    e.message ?: "Błąd filtrowania filmów"
                )
            }
        }
    }

    fun retryLoading() {
        loadMovies()
    }

    // ===== STATISTICS =====

    fun getStatistics(): Map<String, Int> = mapOf(
        "liked" to sessionLiked.size,
        "disliked" to sessionDisliked.size,
        "watched" to movieHistory.size
    )

    // ===== LETTERBOXD IMPORT =====

    fun importFromLetterboxd(username: String) {
        viewModelScope.launch {
            _letterboxdState.value = LetterboxdState.Loading()

            try {
                val result = letterboxdRepository.importFromLetterboxd(
                    username = username,
                    onProgress = { current, total ->
                        _letterboxdState.value = LetterboxdState.Loading(current, total)
                    }
                )

                if (result.success) {
                    _letterboxdState.value = LetterboxdState.Success(result)
                    // watchedMovies is a Flow, so it auto-updates
                } else {
                    _letterboxdState.value = LetterboxdState.Error(result.message)
                }
            } catch (e: Exception) {
                _letterboxdState.value = LetterboxdState.Error(
                    e.message ?: "Nieznany błąd"
                )
            }
        }
    }

    fun resetLetterboxdState() {
        _letterboxdState.value = LetterboxdState.Idle
    }
}