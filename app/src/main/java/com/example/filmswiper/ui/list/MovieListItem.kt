package com.example.filmswiper.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.filmswiper.data.Movie

@Composable
fun MovieListItem(
    movie: Movie,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "Poster for ${movie.title}",
                modifier = Modifier.size(width = 80.dp, height = 120.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

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
            }
        }
    }
}
