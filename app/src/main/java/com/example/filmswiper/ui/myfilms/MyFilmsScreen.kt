// FILE: MyFilmsScreen.kt
package com.example.filmswiper.ui.myfilms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.filmswiper.data.Movie
import com.example.filmswiper.ui.swipe.SwipeViewModel

@Composable
fun MyFilmsScreen(
    viewModel: SwipeViewModel = viewModel()
) {
    val watchedMovies by viewModel.watchedMovies.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (watchedMovies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Brak obejrzanych filmów 🍿",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchedMovies) { (movie, rating) ->
                    WatchedMovieCard(movie = movie, rating = rating)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun WatchedMovieCard(
    movie: Movie,
    rating: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Plakat
            movie.posterUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Poster for ${movie.title}",
                    modifier = Modifier
                        .size(width = 80.dp, height = 120.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rok: ${movie.year ?: "?"}",
                    style = MaterialTheme.typography.bodySmall
                )

                val genresText = if (movie.genres.isEmpty()) "-" else movie.genres.joinToString(", ")
                Text(
                    text = "Gatunki: $genresText",
                    style = MaterialTheme.typography.bodySmall
                )

                movie.rating?.let {
                    Text(
                        text = "TMDb: ${"%.1f".format(it)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating bars
                RatingBars(rating = rating)
            }
        }
    }
}

@Composable
private fun RatingBars(rating: Int) {
    Column {
        Text(
            text = "Twoja ocena: $rating / 10",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            repeat(10) { index ->
                val isRated = index < rating
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = if (isRated) {
                            // Gradient od niebieskiego do zielonego
                            val hue = 120f * (index / 9f) // 0° (czerwony) -> 120° (zielony)
                            Color.hsv(hue, 0.7f, 0.8f)
                        } else {
                            Color.LightGray
                        }
                    ) {}
                }
            }
        }
    }
}