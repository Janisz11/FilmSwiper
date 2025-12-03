// FILE: ui/profile/ProfileScreen.kt
package com.example.filmswiper.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

@Composable
fun ProfileScreen(
    viewModel: SwipeViewModel,
    onNavigateToImport: () -> Unit,
    onNavigateToMyFilms: () -> Unit
) {
    val watchedMoviesWithRatings by viewModel.watchedMovies.collectAsState(initial = emptyList())
    val watchedMovies = watchedMoviesWithRatings.map { MovieMapper.toDomainMovieWithRating(it) }

    // Oblicz statystyki
    val totalWatched = watchedMovies.size
    val averageRating = if (watchedMovies.isNotEmpty()) {
        watchedMovies.mapNotNull { it.userRating }.average()
    } else 0.0

    // Ulubiony gatunek
    val favoriteGenre = watchedMovies
        .flatMap { it.genres }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key ?: "-"

    // Stan rozwijanych sekcji
    var isStatsExpanded by remember { mutableStateOf(true) }
    var isImportExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mój Profil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // === STATYSTYKI ===
        item {
            ExpandableSection(
                title = "Statystyki",
                icon = Icons.Default.BarChart,
                isExpanded = isStatsExpanded,
                onToggle = { isStatsExpanded = !isStatsExpanded }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        icon = Icons.Default.Movie,
                        value = totalWatched.toString(),
                        label = "Obejrzane"
                    )
                    StatCard(
                        icon = Icons.Default.Star,
                        value = if (averageRating > 0) String.format("%.1f", averageRating) else "-",
                        label = "Średnia",
                        valueColor = if (averageRating > 0) getRatingColor(averageRating.toInt()) else null
                    )
                    StatCard(
                        icon = Icons.Default.Favorite,
                        value = favoriteGenre,
                        label = "Top gatunek",
                        isSmallText = true
                    )
                }

                // Rozkład ocen z kolorami
                if (watchedMovies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rozkład ocen",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val ratingCounts = (1..10).associateWith { rating ->
                        watchedMovies.count { it.userRating == rating }
                    }
                    val maxCount = ratingCounts.values.maxOrNull() ?: 1

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..10).forEach { rating ->
                            val count = ratingCounts[rating] ?: 0
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(
                                            if (maxCount > 0)
                                                (60 * (count.toFloat() / maxCount)).dp.coerceAtLeast(4.dp)
                                            else
                                                4.dp
                                        )
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(getRatingColor(rating))
                                )
                                Text(
                                    text = rating.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = getRatingColor(rating)
                                )
                            }
                        }
                    }
                }
            }
        }

        // === MOJE FILMY - PRZYCISK DO NAWIGACJI ===
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMyFilms() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Moje filmy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$totalWatched obejrzanych filmów",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Otwórz",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // === IMPORT ===
        item {
            Spacer(modifier = Modifier.height(4.dp))
            ExpandableSection(
                title = "Import danych",
                icon = Icons.Default.CloudDownload,
                isExpanded = isImportExpanded,
                onToggle = { isImportExpanded = !isImportExpanded },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Zaimportuj swoje oceny z innych platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = onNavigateToImport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.ImportExport,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importuj z Letterboxd")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Icon(
                        Icons.Default.ImportExport,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importuj z IMDb (wkrótce)")
                }
            }
        }

        // Footer
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "FilmSwiper v1.0 • Dane z TMDB",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// === KOMPONENTY ===

@Composable
private fun ExpandableSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Zwiń" else "Rozwiń"
                )
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    isSmallText: Boolean = false,
    valueColor: Color? = null
) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = if (isSmallText)
                MaterialTheme.typography.bodyMedium
            else
                MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}