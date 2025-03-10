package com.example.project3

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

import com.example.project3_version2.Movie

@Entity(foreignKeys = [ForeignKey(
    entity = Movie::class,
    parentColumns = ["movieId"],
    childColumns = ["movieId"],
    onDelete = ForeignKey.CASCADE
)])
data class FunFact (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name= "movieId") val movieId: Int,
    @ColumnInfo(name= "description") val description: String
)