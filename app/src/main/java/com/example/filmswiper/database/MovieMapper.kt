// FILE: MovieMapper.kt
package com.example.filmswiper.database

import com.example.filmswiper.data.Movie
import com.example.filmswiper.data.MovieStatus
import com.example.filmswiper.database.entity.MovieEntity
import com.example.filmswiper.database.entity.MovieWithRating
import com.example.filmswiper.database.entity.RatingEntity

/**
 * Mapper class for converting between domain models (Movie)
 * and database entities (MovieEntity, RatingEntity)
 */
object MovieMapper {

    // ===== SINGLE CONVERSIONS =====

    /**
     * Convert domain Movie to Room MovieEntity
     * Used when saving to database
     */
    fun toEntity(movie: Movie): MovieEntity {
        return MovieEntity(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            year = movie.year,
            genres = movie.genres.joinToString(","),  // List → CSV string
            rating = movie.rating,
            posterUrl = movie.posterUrl,
            status = movie.status.name,  // Enum → String
            lastUpdated = System.currentTimeMillis()
        )
    }

    /**
     * Convert Room MovieEntity to domain Movie
     * Used when reading from database
     */
    fun toDomain(entity: MovieEntity): Movie {
        return Movie(
            id = entity.id,
            title = entity.title,
            overview = entity.overview,
            year = entity.year,
            genres = entity.genres
                .split(",")
                .filter { it.isNotEmpty() }  // Remove empty strings,
                    rating = entity.rating,
            posterUrl = entity.posterUrl,
            status = MovieStatus.valueOf(entity.status),  // String → Enum
            userRating = null  // Will be populated from RatingEntity
        )
    }

    /**
     * Convert RatingEntity to user rating (Int)
     */
    fun toUserRating(rating: RatingEntity?): Int? {
        return rating?.userRating
    }

    // ===== COMBINED CONVERSIONS =====

    /**
     * Convert MovieWithRating (join result) to Pair<Movie, Int?>
     * This includes both the movie and its user rating
     */
    fun toDomainWithRating(movieWithRating: MovieWithRating): Pair<Movie, Int?> {
        val movie = toDomain(movieWithRating.movie)
        val userRating = movieWithRating.ratings.firstOrNull()?.userRating

        return movie to userRating
    }

    /**
     * Convert MovieWithRating to Movie with userRating field populated
     */
    fun toDomainMovieWithRating(movieWithRating: MovieWithRating): Movie {
        val movie = toDomain(movieWithRating.movie)
        val userRating = movieWithRating.ratings.firstOrNull()?.userRating

        return movie.copy(userRating = userRating)
    }

    // ===== BATCH CONVERSIONS =====

    /**
     * Convert list of domain Movies to Room MovieEntities
     */
    fun toEntities(movies: List<Movie>): List<MovieEntity> {
        return movies.map { toEntity(it) }
    }

    /**
     * Convert list of Room MovieEntities to domain Movies
     */
    fun toDomainList(entities: List<MovieEntity>): List<Movie> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of MovieWithRating to list of Pair<Movie, Int?>
     */
    fun toDomainListWithRatings(
        moviesWithRatings: List<MovieWithRating>
    ): List<Pair<Movie, Int?>> {
        return moviesWithRatings.map { toDomainWithRating(it) }
    }

    /**
     * Convert list of MovieWithRating to list of Movies with ratings
     */
    fun toDomainMovieListWithRatings(
        moviesWithRatings: List<MovieWithRating>
    ): List<Movie> {
        return moviesWithRatings.map { toDomainMovieWithRating(it) }
    }

    // ===== RATING CONVERSIONS =====

    /**
     * Create RatingEntity from movieId and user rating
     */
    fun createRatingEntity(
        movieId: Long,
        userRating: Int,
        source: String = "local"
    ): RatingEntity {
        return RatingEntity(
            movieId = movieId,
            userRating = userRating,
            source = source,
            ratedAt = System.currentTimeMillis()
        )
    }

    // ===== HELPER FUNCTIONS =====

    /**
     * Split genres CSV string to List<String>
     */
    fun parseGenres(genresString: String): List<String> {
        return genresString
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /**
     * Join genres List to CSV string
     */
    fun formatGenres(genres: List<String>): String {
        return genres.joinToString(",")
    }
}