package com.example.filmswiper.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieFilter
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.data.remote.RemoteMovieRepository
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

    private val remoteRepository = RemoteMovieRepository(
        api = NetworkModule.movieApi,
        apiKey = "8d0ba6c554df6264bc090e7cec044811"
    )

    private val sessionLiked = mutableSetOf<Long>()
    private val sessionDisliked = mutableSetOf<Long>()
    private val sessionWatched = mutableMapOf<Long, Int>()

    private val _uiState = MutableStateFlow<SwipeUiState>(SwipeUiState.Loading)
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _genres = MutableStateFlow<Map<Int, String>>(emptyMap())
    val genres: StateFlow<Map<Int, String>> = _genres.asStateFlow()

    private val _watchedMovies = MutableStateFlow<List<Pair<Movie, Int>>>(emptyList())
    val watchedMovies: StateFlow<List<Pair<Movie, Int>>> = _watchedMovies.asStateFlow()

    private val movieHistory = mutableListOf<Movie>()

    init {
        loadMovies()
        loadGenres()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            try {
                val movies = remoteRepository.getMoviesMixed(count = 50)
                _allMovies.value = movies
                loadNextMovie()
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(
                    e.message ?: "Błąd pobierania filmów"
                )
            }
        }
    }

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
                sessionWatched[movie.id] = rating
                movieHistory.add(movie)

                // Dodaj do listy obejrzanych
                val newWatchedMovies = _watchedMovies.value.toMutableList()
                newWatchedMovies.add(movie to rating)
                _watchedMovies.value = newWatchedMovies

                println(" Film obejrzany: ${movie.title} | Ocena: $rating")
                println(" Wszystkie obejrzane: ${_watchedMovies.value.size}")
            } else {
                println(" uiState nie jest Success: $state")
            }
        }
        loadNextMovie()
    }

    fun undoLastAction() {
        if (movieHistory.isNotEmpty()) {
            val lastMovie = movieHistory.removeAt(movieHistory.size - 1)
            sessionLiked.remove(lastMovie.id)
            sessionDisliked.remove(lastMovie.id)
            sessionWatched.remove(lastMovie.id)
            _uiState.value = SwipeUiState.Success(lastMovie)
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

    fun retryLoading() {
        loadMovies()
    }

    fun applyFilter(filter: MovieFilter) {
        viewModelScope.launch {
            _uiState.value = SwipeUiState.Loading
            try {
                val movies = remoteRepository.getMoviesByFilter(filter, count = 50)
                _allMovies.value = movies
                loadNextMovie()
            } catch (e: Exception) {
                _uiState.value = SwipeUiState.Error(
                    e.message ?: "Błąd filtrowania filmów"
                )
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

    fun getStatistics(): Map<String, Int> = mapOf(
        "liked" to sessionLiked.size,
        "disliked" to sessionDisliked.size,
        "watched" to sessionWatched.size
    )
}