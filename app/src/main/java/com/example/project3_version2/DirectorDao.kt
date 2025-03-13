package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectorDao {
    @Insert
    suspend fun insert(director: Director)

    @Query("SELECT * FROM director")
    fun getAll(): Flow<List<Director>>

    @Query("Delete From Director")
    suspend fun deleteAllDirectors()

    @Query("SELECT * FROM director WHERE name LIKE :query")
    suspend fun searchDirectors(query: String): List<Director>
}