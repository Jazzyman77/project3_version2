package com.example.project3_version2

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAll()

    suspend fun insert(movie:Movie) {
        movieDao.insert(movie)
    }
    suspend fun update(movie: Movie) {
        movieDao.update(movie)
    }

    suspend fun delete(movie: Movie) {
        movieDao.delete(movie)
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return movieDao.searchMovies("%$query%") // Runs in coroutine
    }

}