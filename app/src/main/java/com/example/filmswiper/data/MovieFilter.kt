// FILE: MovieFilter.kt
package com.example.filmswiper.data

data class MovieFilter(
    val title: String = "",
    val genreIds: List<Int> = emptyList(),
    val yearFrom: Int? = null,
    val yearTo: Int? = null
) {
    fun isEmpty(): Boolean {
        return title.isEmpty() && genreIds.isEmpty() && yearFrom == null && yearTo == null
    }
}

