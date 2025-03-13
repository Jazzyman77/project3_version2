package com.example.project3_version2

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao, private val directorDao: DirectorDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAll()
    val allDirectors: Flow<List<Director>> = directorDao.getAll()

    suspend fun insertMovie(movie:Movie) {
        movieDao.insert(movie)
    }
    suspend fun updateMovie(movie: Movie) {
        movieDao.update(movie)
    }

    suspend fun deleteMovie(movie: Movie) {
        movieDao.delete(movie)
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return movieDao.searchMovies("%$query%") // Runs in coroutine
    }
    suspend fun getValidDirectorIds(): List<Int> = movieDao.getValidParentIds()


}