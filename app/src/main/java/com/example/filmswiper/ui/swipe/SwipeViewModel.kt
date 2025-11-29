package com.example.filmswiper.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.data.remote.RemoteMovieRepository
import com.example.filmswiper.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SwipeViewModel : ViewModel() {

    // 👇 UWAGA: konstruktor BEZ parametrów – wymagane, żeby viewModel() działało
    private val remoteRepository = RemoteMovieRepository(
        api = NetworkModule.movieApi,
        apiKey = "8d0ba6c554df6264bc090e7cec044811" // tutaj wkleisz swój klucz TMDb
    )

    private val sessionLiked = mutableSetOf<Long>()
    private val sessionDisliked = mutableSetOf<Long>()

    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies

    init {
        viewModelScope.launch {
            try {
                val movies = remoteRepository.getMovies()
                _allMovies.value = movies
                loadNextMovie()
            } catch (e: Exception) {
                // na razie tylko logika awaryjna – brak filmów = currentMovie=null
                e.printStackTrace()
                _allMovies.value = emptyList()
                _currentMovie.value = null
            }
        }
    }

    fun onSwipeRight() {
        _currentMovie.value?.let { movie ->
            sessionLiked += movie.id
        }
        loadNextMovie()
    }

    fun onSwipeLeft() {
        _currentMovie.value?.let { movie ->
            sessionDisliked += movie.id
        }
        loadNextMovie()
    }

    fun onBlacklist() {
        _currentMovie.value?.let { movie ->
            sessionDisliked += movie.id
        }
        loadNextMovie()
    }

    fun onMarkAsWatched(rating: Int) {
        _currentMovie.value?.let { movie ->
            sessionLiked += movie.id
            // docelowo tu będzie zapis do lokalnej bazy z userRating
        }
        loadNextMovie()
    }

    private fun loadNextMovie() {
        viewModelScope.launch {
            val all = _allMovies.value
            if (all.isEmpty()) {
                _currentMovie.value = null
                return@launch
            }

            val excludeIds = sessionLiked + sessionDisliked

            val candidates = all.filter { movie ->
                movie.status != MovieStatus.WATCHED_RATED &&
                        movie.status != MovieStatus.BLACKLISTED &&
                        movie.id !in excludeIds
            }

            _currentMovie.value = candidates.randomOrNull()
        }
    }

    fun getAllMovies(): List<Movie> = _allMovies.value
}
