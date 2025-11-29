package com.example.filmswiper.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieRepository
import com.example.filmswiper.data.MovieStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SwipeViewModel(
    // Na razie tworzymy repo ręcznie – później można to wstrzyknąć przez DI
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val sessionLiked = mutableSetOf<Long>()
    private val sessionDisliked = mutableSetOf<Long>()

    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    init {
        loadNextMovie()
    }

    fun onSwipeRight() {
        _currentMovie.value?.let { movie ->
            sessionLiked += movie.id
            // TODO: update preferencji sesyjnych pod rekomendacje
        }
        loadNextMovie()
    }

    fun onSwipeLeft() {
        _currentMovie.value?.let { movie ->
            sessionDisliked += movie.id
            // TODO: update preferencji sesyjnych pod rekomendacje
        }
        loadNextMovie()
    }

    fun onBlacklist() {
        val movie = _currentMovie.value ?: return
        viewModelScope.launch {
            val updated = movie.copy(status = MovieStatus.BLACKLISTED)
            repository.updateMovie(updated)
            loadNextMovie()
        }
    }

    fun onMarkAsWatched(rating: Int) {
        val movie = _currentMovie.value ?: return
        viewModelScope.launch {
            val updated = movie.copy(
                status = MovieStatus.WATCHED_RATED,
                userRating = rating
            )
            repository.updateMovie(updated)
            loadNextMovie()
        }
    }

    private fun loadNextMovie() {
        viewModelScope.launch {
            val all = repository.getAllMovies()

            val excludeIds = sessionLiked + sessionDisliked

            val candidates = all.filter { movie ->
                movie.status != MovieStatus.WATCHED_RATED &&
                        movie.status != MovieStatus.BLACKLISTED &&
                        movie.id !in excludeIds
            }

            // TODO: tutaj później podłączysz RecommendationEngine zamiast random
            _currentMovie.value = candidates.randomOrNull()
        }
    }
}
