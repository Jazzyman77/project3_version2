package com.example.project3_version2

import androidx.room.Embedded
import androidx.room.Relation
import com.example.project3.FunFact


data class MovieFunFact (
    @Embedded val movie: Movie,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "movieId"
    )
    val funFacts: List<FunFact>
)