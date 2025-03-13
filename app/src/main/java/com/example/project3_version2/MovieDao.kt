package com.example.project3_version2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Query("SELECT * FROM movie")
    fun getAll(): Flow<List<Movie>>

    @Delete
    suspend fun delete(movie: Movie)

    @Query("DELETE FROM movie")
    suspend fun deleteAllMovies()

    @Query("SELECT * FROM movie WHERE title LIKE :query")
    suspend fun searchMovies(query: String): List<Movie> // Now returns List<Movie>
}