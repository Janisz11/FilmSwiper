package com.example.filmswiper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.filmswiper.ui.swipe.SwipeScreen
import com.example.filmswiper.ui.theme.FilmSwiperTheme // 👈 UPEWNIJ SIĘ co masz w Theme.kt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            FilmSwiperTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SwipeScreen()
                }
            }
        }
    }
}
