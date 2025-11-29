package com.example.filmswiper.ui.swipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.filmswiper.data.Movie

@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel = viewModel()
) {
    val movie by viewModel.currentMovie.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (movie == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Brak filmów do wyświetlenia 🙃")
            }
        } else {
            MovieCard(
                movie = movie!!,
                onLike = { viewModel.onSwipeRight() },
                onDislike = { viewModel.onSwipeLeft() },
                onBlacklist = { viewModel.onBlacklist() },
                onMarkWatched = { rating -> viewModel.onMarkAsWatched(rating) }
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onBlacklist: () -> Unit,
    onMarkWatched: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // 👇 plakat filmu (jeśli jest URL)
            movie.posterUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Poster for ${movie.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rok: ${movie.year ?: "?"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Gatunki: ${
                    if (movie.genres.isEmpty()) "-" else movie.genres.joinToString()
                }",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = onDislike) {
                    Text("Nie dziś 👎")
                }
                Button(onClick = onLike) {
                    Text("Może dziś 👍")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = onBlacklist) {
                    Text("Nigdy więcej 🚫")
                }
                OutlinedButton(onClick = { onMarkWatched(8) /* TODO: UI oceny */ }) {
                    Text("Obejrzany ✅")
                }
            }
        }
    }
}
