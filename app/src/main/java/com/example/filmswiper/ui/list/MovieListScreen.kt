package com.example.filmswiper.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filmswiper.data.Movie
import com.example.filmswiper.ui.swipe.SwipeViewModel

@Composable
fun MovieListScreen(
    viewModel: SwipeViewModel = viewModel()
) {
    // obserwujemy stan z ViewModelu
    val movies by viewModel.allMovies.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ładowanie filmów...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(movies) { movie ->
                    MovieListItem(movie = movie)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun MovieListItem(
    movie: Movie
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // TODO: później można dodać przejście do ekranu szczegółów
            }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Rok: ${movie.year ?: "?"} · Gatunki: ${movie.genres.joinToString()}",
            style = MaterialTheme.typography.bodySmall
        )
        movie.rating?.let {
            Text(
                text = "Ocena: $it",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
