package com.example.project3_version2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project3.FunFact

@Database(entities = [Movie::class, Director::class, FunFact::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun directorDao(): DirectorDao
    abstract fun funFactDao(): FunFactDao
    abstract fun directorMovieDao(): DirectorMovieDao
    abstract fun movieFunFactDao(): MovieFunFactDao

    companion object {
        // Volatile annotation ensures the value of INSTANCE is updated across all threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Method to get the singleton instance of AppDatabase
        fun getDatabase(context: Context): AppDatabase {
            // If the instance is not null, return it
            return INSTANCE ?: synchronized(this) {
                // If the instance is null, create it
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    //.createFromAsset("srcDataBase.db")
                    .build()

                // Assign the instance to the static variable INSTANCE for future use
                INSTANCE = instance
                instance
            }
        }

    }
}