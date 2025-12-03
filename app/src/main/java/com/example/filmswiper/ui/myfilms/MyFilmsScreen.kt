// FILE: ui/profile/MyFilmsScreen.kt
package com.example.filmswiper.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.filmswiper.data.Movie
import com.example.filmswiper.database.MovieMapper
import com.example.filmswiper.ui.swipe.SwipeViewModel

// === GRADIENT KOLORÓW DLA OCEN ===
private fun getRatingColor(rating: Int): Color {
    return when {
        rating <= 1 -> Color(0xFFD32F2F)
        rating == 2 -> Color(0xFFE64A19)
        rating == 3 -> Color(0xFFF57C00)
        rating == 4 -> Color(0xFFFFA000)
        rating == 5 -> Color(0xFFFFC107)
        rating == 6 -> Color(0xFFCDDC39)
        rating == 7 -> Color(0xFF8BC34A)
        rating == 8 -> Color(0xFF4CAF50)
        rating == 9 -> Color(0xFF388E3C)
        else -> Color(0xFF1B5E20)
    }
}

private fun getTextColorForRating(rating: Int): Color {
    return when {
        rating in 4..6 -> Color.Black
        else -> Color.White
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFilmsScreen(
    viewModel: SwipeViewModel,
    onBack: () -> Unit
) {
    val watchedMoviesWithRatings by viewModel.watchedMovies.collectAsState(initial = emptyList())
    val watchedMovies = watchedMoviesWithRatings.map { MovieMapper.toDomainMovieWithRating(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje filmy (${watchedMovies.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (watchedMovies.isEmpty()) {
            // Pusty stan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.MovieFilter,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Brak obejrzanych filmów",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Zacznij swipe'ować lub zaimportuj z Letterboxd!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(watchedMovies) { movie ->
                    MovieListItem(movie = movie)
                }
            }
        }
    }
}

@Composable
private fun MovieListItem(movie: Movie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                movie.year?.let { year ->
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (movie.genres.isNotEmpty()) {
                    Text(
                        text = movie.genres.take(3).joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Rating z gradientem kolorów
            movie.userRating?.let { rating ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = getRatingColor(rating)
                ) {
                    Text(
                        text = "$rating",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = getTextColorForRating(rating)
                    )
                }
            }
        }
    }
}