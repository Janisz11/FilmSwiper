package com.example.filmswiper.data

class MovieRepository {

    // Na razie prosta baza w pamięci – później można to podmienić na Room/API
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
        ),
        Movie(
            id = 5L,
            title = "Interstellar",
            overview = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
            year = 2014,
            genres = listOf("Sci-Fi", "Drama"),
            rating = 8.6
        ),
        Movie(
            id = 6L,
            title = "Fight Club",
            overview = "An insomniac office worker and a soap maker form an underground fight club.",
            year = 1999,
            genres = listOf("Drama"),
            rating = 8.8
        ),
        Movie(
            id = 7L,
            title = "Whiplash",
            overview = "A young drummer enrolls at a cut-throat music conservatory.",
            year = 2014,
            genres = listOf("Drama", "Music"),
            rating = 8.5
        ),
        Movie(
            id = 8L,
            title = "The Matrix",
            overview = "A computer hacker learns about the true nature of reality and his role in the war.",
            year = 1999,
            genres = listOf("Sci-Fi", "Action"),
            rating = 8.7
        ),
        Movie(
            id = 9L,
            title = "Pulp Fiction",
            overview = "The lives of two mob hitmen, a boxer, and others intertwine in a tale of violence and redemption.",
            year = 1994,
            genres = listOf("Crime", "Drama"),
            rating = 8.9
        ),
        Movie(
            id = 10L,
            title = "The Social Network",
            overview = "The story of the founders of Facebook.",
            year = 2010,
            genres = listOf("Drama", "Biography"),
            rating = 7.8
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
