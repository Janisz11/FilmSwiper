// FILE: SwipeScreen.kt
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.filmswiper.data.Movie

@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SwipeUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is SwipeUiState.Empty -> {
                    Text(
                        "Koniec filmów! 🎬",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
                is SwipeUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            "❌ Błąd",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text((uiState as SwipeUiState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retryLoading() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
                is SwipeUiState.Success -> {
                    val movie = (uiState as SwipeUiState.Success).movie
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MovieCard(
                            movie = movie,
                            onLike = { viewModel.onSwipeRight() },
                            onDislike = { viewModel.onSwipeLeft() },
                            onBlacklist = { viewModel.onBlacklist() },
                            onMarkWatched = { rating -> viewModel.onMarkAsWatched(rating) }
                        )
                    }
                }
            }
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
    var showRatingDialog by remember { mutableIntStateOf(0) }

    if (showRatingDialog > 0) {
        RatingDialog(
            initialRating = showRatingDialog,
            onRatingSelected = { rating ->
                println("✅ Wysyłam ocenę: $rating dla ${movie.title}")
                onMarkWatched(rating)
                showRatingDialog = 0
            },
            onDismiss = { showRatingDialog = 0 }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                movie.posterUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Poster for ${movie.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Rok: ${movie.year ?: "?"}",
                    style = MaterialTheme.typography.bodySmall
                )

                val genresText = if (movie.genres.isEmpty()) "-" else movie.genres.joinToString()
                Text(
                    text = "Gatunki: $genresText",
                    style = MaterialTheme.typography.bodySmall
                )

                movie.rating?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ocena TMDb: ${"%.1f".format(it)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDislike
                    ) {
                        Text("Nie dziś 👎")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onLike
                    ) {
                        Text("Może dziś 👍")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onBlacklist
                    ) {
                        Text("Nigdy więcej ❌")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            println("🔵 Kliknięto 'Obejrzany'")
                            showRatingDialog = 5
                        }
                    ) {
                        Text("Obejrzany ✓")
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingDialog(
    initialRating: Int,
    onRatingSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableIntStateOf(initialRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jak oceniasz ten film?") },
        text = {
            Column {
                Text("Ocena: $rating / 10", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(12.dp))

                // Gradient bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(10) { index ->
                        val isRated = index < rating
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            shape = RoundedCornerShape(4.dp),
                            color = if (isRated) {
                                val hue = 120f * (index / 9f)
                                Color.hsv(hue, 0.7f, 0.8f)
                            } else {
                                Color.LightGray
                            }
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Przyciski 1-10
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (1..10).forEach { num ->
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            onClick = { rating = num }
                        ) {
                            Text(num.toString(), fontSize = MaterialTheme.typography.bodySmall.fontSize)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                println("✅ Potwierdzam ocenę: $rating")
                onRatingSelected(rating)
            }) {
                Text("Zatwierdź")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}