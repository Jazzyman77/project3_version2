package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DirectorDao {
    @Insert
    suspend fun insert(director: Director)

    @Query("SELECT * FROM director")
    suspend fun getAll(): List<Director>

    @Query("Delete From Director")
    suspend fun deleteAllDirectors()

}