// FILE: ui/import/LetterboxdImportScreen.kt
package com.example.filmswiper.ui.import

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.filmswiper.ui.swipe.SwipeViewModel

@Composable
fun LetterboxdImportScreen(
    viewModel: SwipeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.letterboxdState.collectAsState()
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Import z Letterboxd",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Zaimportuj obejrzane filmy i oceny z Letterboxd",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nazwa użytkownika Letterboxd") },
            placeholder = { Text("np. Janisz11") },
            singleLine = true,
            enabled = state !is SwipeViewModel.LetterboxdState.Loading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.importFromLetterboxd(username) },
            enabled = username.isNotBlank() && state !is SwipeViewModel.LetterboxdState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Importuj z Letterboxd")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // State display
        when (val currentState = state) {
            is SwipeViewModel.LetterboxdState.Loading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))

                        if (currentState.total > 0) {
                            Text(
                                "Importowanie: ${currentState.current} / ${currentState.total}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = currentState.current.toFloat() / currentState.total,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                "Pobieranie danych z Letterboxd...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "To może potrwać 1-2 minuty",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is SwipeViewModel.LetterboxdState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Import zakończony!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Zaimportowano: ${currentState.result.importedCount} filmów",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (currentState.result.skippedCount > 0) {
                            Text(
                                "Pominięto: ${currentState.result.skippedCount} (nie znaleziono w TMDB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Wróć do aplikacji")
                        }
                    }
                }
            }

            is SwipeViewModel.LetterboxdState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Błąd importu",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            currentState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { viewModel.resetLetterboxdState() }) {
                            Text("Spróbuj ponownie")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}