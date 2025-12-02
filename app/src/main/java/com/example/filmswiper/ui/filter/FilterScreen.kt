// FILE: FilterScreen.kt
package com.example.filmswiper.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filmswiper.data.MovieFilter
import com.example.filmswiper.ui.swipe.SwipeViewModel

@Composable
fun FilterScreen(
    viewModel: SwipeViewModel = viewModel(),
    onApplyFilter: (MovieFilter) -> Unit
) {
    val genres by viewModel.genres.collectAsState(initial = emptyMap())

    var titleInput by remember { mutableStateOf("") }
    val selectedGenreIds = remember { mutableStateListOf<Int>() }
    var yearFrom by remember { mutableStateOf("") }
    var yearTo by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Filtruj Filmy 🎬",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Szukaj po tytule
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Po tytule 📝",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("np. Avatar, Inception...") },
                                singleLine = true
                            )
                        }
                    }
                }

                // Rok wydania
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Rok wydania 📅",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = yearFrom,
                                    onValueChange = { yearFrom = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Od") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = yearTo,
                                    onValueChange = { yearTo = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Do") },
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                // Gatunki
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Gatunki 🎭",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                items(genres.toList()) { (genreId, genreName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = genreId in selectedGenreIds,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedGenreIds.add(genreId)
                                } else {
                                    selectedGenreIds.remove(genreId)
                                }
                            }
                        )
                        Text(genreName, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Przyciski
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        titleInput = ""
                        selectedGenreIds.clear()
                        yearFrom = ""
                        yearTo = ""
                    }
                ) {
                    Text("Wyczyść")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val filter = MovieFilter(
                            title = titleInput,
                            genreIds = selectedGenreIds.toList(),
                            yearFrom = yearFrom.toIntOrNull(),
                            yearTo = yearTo.toIntOrNull()
                        )
                        onApplyFilter(filter)
                    }
                ) {
                    Text("Zastosuj ✓")
                }
            }
        }
    }
}