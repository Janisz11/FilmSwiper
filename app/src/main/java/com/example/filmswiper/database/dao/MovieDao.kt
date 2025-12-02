// FILE: MovieDao.kt
package com.example.filmswiper.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.filmswiper.database.entity.MovieEntity
import com.example.filmswiper.database.entity.RatingEntity
import com.example.filmswiper.database.entity.MovieWithRating
import com.example.filmswiper.database.entity.RatingDistribution
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    // ===== MOVIES =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): MovieEntity?

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE status = :status")
    suspend fun getMoviesByStatus(status: String): List<MovieEntity>

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()

    // ===== RATINGS =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: RatingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRatings(ratings: List<RatingEntity>)

    @Update
    suspend fun updateRating(rating: RatingEntity)

    @Delete
    suspend fun deleteRating(rating: RatingEntity)

    @Query("SELECT * FROM ratings WHERE movieId = :movieId")
    suspend fun getRatingsForMovie(movieId: Long): List<RatingEntity>

    @Query("SELECT * FROM ratings ORDER BY ratedAt DESC")
    suspend fun getAllRatings(): List<RatingEntity>

    @Query("SELECT AVG(userRating) FROM ratings")
    suspend fun getAverageRating(): Double?

    @Query("SELECT COUNT(*) FROM ratings")
    suspend fun getRatingCount(): Int

    // ===== COMBINED QUERIES =====

    @Transaction
    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieWithRatings(movieId: Long): MovieWithRating?

    @Transaction
    @Query("SELECT m.* FROM movies m LEFT JOIN ratings r ON m.id = r.movieId ORDER BY r.ratedAt DESC")
    suspend fun getAllMoviesWithRatings(): List<MovieWithRating>

    @Transaction
    @Query("""
        SELECT m.* FROM movies m 
        INNER JOIN ratings r ON m.id = r.movieId 
        ORDER BY r.ratedAt DESC
    """)
    suspend fun getWatchedMoviesWithRatings(): List<MovieWithRating>

    // ===== WATCHED MOVIES FLOW (Real-time) =====

    @Transaction
    @Query("""
        SELECT m.* FROM movies m 
        INNER JOIN ratings r ON m.id = r.movieId 
        ORDER BY r.ratedAt DESC
    """)
    fun getWatchedMoviesFlow(): Flow<List<MovieWithRating>>

    // ===== STATISTICS =====

    @Query("""
        SELECT COUNT(*) FROM movies m 
        INNER JOIN ratings r ON m.id = r.movieId
    """)
    suspend fun getWatchedMovieCount(): Int

    @Query("""
        SELECT AVG(r.userRating) FROM ratings r
    """)
    suspend fun getAverageUserRating(): Float?

    @Query("""
        SELECT r.userRating, COUNT(*) as count
        FROM ratings r
        GROUP BY r.userRating
        ORDER BY r.userRating DESC
    """)
    suspend fun getRatingDistribution(): List<RatingDistribution>
}