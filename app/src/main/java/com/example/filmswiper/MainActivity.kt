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
import com.example.filmswiper.ui.list.MovieListScreen
import com.example.filmswiper.ui.myfilms.MyFilmsScreen
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
                            // ✨ Lupka do filtrowania
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
                            Button(onClick = { selectedTab = "myfilms" }) {
                                Text("Moje 🎥")
                            }
                            Button(onClick = { selectedTab = "list" }) {
                                Text("Lista 📃")
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
                            "myfilms" -> MyFilmsScreen(viewModel)
                            "list" -> MovieListScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}