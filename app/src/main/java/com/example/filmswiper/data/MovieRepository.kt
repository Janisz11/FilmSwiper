package com.example.filmswiper.data

class MovieRepository {

    // Na razie tylko lista w pamięci – później można to podmienić na Room/API
    private val movies = mutableListOf(
        Movie(
            id = 1L,
            title = "Inception",
            overview = "A thief who steals corporate secrets through dream-sharing technology.",
            year = 2010,
            genres = listOf("Sci-Fi", "Action"),
            rating = 8.8
        ),
        Movie(
            id = 2L,
            title = "The Dark Knight",
            overview = "Batman faces the Joker in Gotham City.",
            year = 2008,
            genres = listOf("Action", "Crime"),
            rating = 9.0
        ),
        Movie(
            id = 3L,
            title = "La La Land",
            overview = "A jazz pianist and an aspiring actress fall in love in Los Angeles.",
            year = 2016,
            genres = listOf("Romance", "Drama", "Musical"),
            rating = 8.0
        ),
        Movie(
            id = 4L,
            title = "Spirited Away",
            overview = "A young girl enters a world of spirits and gods.",
            year = 2001,
            genres = listOf("Animation", "Fantasy"),
            rating = 8.6
        )
    )

    fun getAllMovies(): List<Movie> = movies.toList()

    fun getMovieById(id: Long): Movie? = movies.find { it.id == id }

    fun updateMovie(movie: Movie) {
        val index = movies.indexOfFirst { it.id == movie.id }
        if (index != -1) {
            movies[index] = movie
        }
    }
}
