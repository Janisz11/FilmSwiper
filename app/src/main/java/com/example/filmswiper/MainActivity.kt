// FILE: MainActivity.kt
package com.example.filmswiper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filmswiper.ui.filter.FilterScreen
import com.example.filmswiper.ui.import.LetterboxdImportScreen
import com.example.filmswiper.ui.list.MovieListScreen
import com.example.filmswiper.ui.profile.MyFilmsScreen
import com.example.filmswiper.ui.profile.ProfileScreen
import com.example.filmswiper.ui.swipe.SwipeScreen
import com.example.filmswiper.ui.swipe.SwipeViewModel
import com.example.filmswiper.ui.theme.FilmSwiperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmSwiperTheme {
                var selectedTab by remember { mutableStateOf("swipe") }
                val viewModel: SwipeViewModel = viewModel()

                // Ekrany z własnym topBar (bez głównego Scaffold topBar/bottomBar)
                val fullScreenTabs = listOf("myfilms", "import")

                if (selectedTab in fullScreenTabs) {
                    // Pełnoekranowe widoki bez bottom bar
                    when (selectedTab) {
                        "myfilms" -> MyFilmsScreen(
                            viewModel = viewModel,
                            onBack = { selectedTab = "profile" }
                        )
                        "import" -> LetterboxdImportScreen(
                            viewModel = viewModel,
                            onBack = {
                                viewModel.resetLetterboxdState()
                                selectedTab = "profile"
                            }
                        )
                    }
                } else {
                    // Normalne widoki z navigation bar
                    Scaffold(
                        topBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FilmSwiper",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(onClick = { selectedTab = "filter" }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Filtruj filmy",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { selectedTab = "swipe" }) {
                                    Text("Swipe 🎬")
                                }
                                Button(onClick = { selectedTab = "list" }) {
                                    Text("Lista 📃")
                                }
                                Button(onClick = { selectedTab = "profile" }) {
                                    Text("Profil 👤")
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedTab) {
                                "swipe" -> SwipeScreen(viewModel)
                                "filter" -> FilterScreen(
                                    viewModel = viewModel,
                                    onApplyFilter = { filter ->
                                        viewModel.applyFilter(filter)
                                        selectedTab = "swipe"
                                    }
                                )
                                "list" -> MovieListScreen(viewModel)
                                "profile" -> ProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateToImport = { selectedTab = "import" },
                                    onNavigateToMyFilms = { selectedTab = "myfilms" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
