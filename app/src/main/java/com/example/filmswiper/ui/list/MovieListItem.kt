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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = "Poster for ${movie.title}",
            modifier = Modifier.size(80.dp)
        )

        Column {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

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

            movie.rating?.let {
                Text(
                    text = "Ocena: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
