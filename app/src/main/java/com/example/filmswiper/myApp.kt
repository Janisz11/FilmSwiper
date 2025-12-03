// FILE: MyApp.kt
package com.example.filmswiper

import android.app.Application
import com.example.filmswiper.database.DatabaseModule

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicjalizuj Room Database
        DatabaseModule.initialize(this)
    }
}