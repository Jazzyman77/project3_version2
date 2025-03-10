package com.example.project3_version2
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Director(
    @PrimaryKey(autoGenerate = true) val directorId: Int=1,
    @ColumnInfo(name="name") val name: String
)