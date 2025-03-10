package com.example.project3_version2

import androidx.room.Embedded
import androidx.room.Relation


data class DirectorMovie (
    @Embedded val director: Director,
            @Relation(
                parentColumn = "directorId",
                entityColumn = "directorId"
            )
            val movies: List<Movie>
)