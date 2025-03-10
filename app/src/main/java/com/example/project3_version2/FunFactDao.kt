package com.example.project3_version2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project3.FunFact

@Dao
interface FunFactDao {

    @Insert
    suspend fun insert (funFact: FunFact)

    @Query("SELECT * FROM funFact")
    fun getAll(): List<FunFact>

    @Query("DELETE FROM funfact")
    fun deleteAllFunFacts()

}