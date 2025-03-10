package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DirectorMovieDao {
    @Transaction
    @Query("select * from director")
    suspend fun getDirectorMovies(): List<DirectorMovie>
}