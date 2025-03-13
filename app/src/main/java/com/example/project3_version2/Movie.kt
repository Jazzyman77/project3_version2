package com.example.project3_version2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = Director::class,
    parentColumns = ["directorId"],
    childColumns = ["directorId"],
    onDelete = ForeignKey.CASCADE
)])
data class Movie(
    @PrimaryKey(autoGenerate = true) val movieId: Int = 1,
    @ColumnInfo(name= "title") val title: String,
    @ColumnInfo(name= "releaseDate") val releaseDate: String,
    @ColumnInfo(name="boxOffice") val boxOffice: Long,
    @ColumnInfo(name= "directorId") val directorId: Int
)