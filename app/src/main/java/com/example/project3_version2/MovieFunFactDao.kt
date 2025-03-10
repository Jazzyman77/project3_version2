package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MovieFunFactDao {
    @Transaction
    @Query("select * from movie")
    suspend fun getMovieFunFacts(): List<MovieFunFact>
}