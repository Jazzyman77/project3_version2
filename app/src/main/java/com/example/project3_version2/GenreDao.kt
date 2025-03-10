package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GenreDao {
    @Insert
    suspend fun insert(genre: Genre)

    @Query("SELECT * FROM genre")
    suspend fun getAll(): List<Genre>

    @Query("DELETE FROM genre")
    suspend fun deleteAllGenres()
}