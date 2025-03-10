package com.example.project3_version2

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.project3.FunFact
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var appDatabase: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appDatabase = AppDatabase.getDatabase(this)
        val movieDao = appDatabase.movieDao()
        val directorDao = appDatabase.directorDao()
        val funFactDao = appDatabase.funFactDao()
        val directorMovieDao = appDatabase.directorMovieDao()
        val movieFunFactDao = appDatabase.movieFunFactDao()

        lifecycleScope.launch {

            // Query data
            movieDao.deleteAllMovies()
            directorDao.deleteAllDirectors()
            val dir1 = Director(directorId =1, name= "George Lucas")
            val dir2 = Director(directorId = 2, name= "Christopher Nolan")
           val movie1 = Movie(movieId= 1,title = "Star Wars", releaseDate = "1979", directorId = 1)
            val movie2 = Movie(movieId = 2,title = "Dark Knight", releaseDate = "2008", directorId = 2)
            val funFact1 = FunFact(id= 1, movieId= 1, description = "The original title was 'The Star Wars'.")
            val funFact2 = FunFact(id= 2, movieId= 1, description = "In the original theatrical cut there is a stormtrooper who hits his head.")
            val funFact3 = FunFact(id= 3, movieId= 2, description = "Heath Ledger won a posthumous Oscar for his role as the Joker.")
            val funFact4 = FunFact(id= 4, movieId= 2, description = "The scene when joker messes with a detonator switch was improv, as there was an actual delay before the pyrotechnics went off")


            directorDao.insert(dir1)
            directorDao.insert(dir2)
            movieDao.insert(movie1)
            movieDao.insert((movie2))
            funFactDao.insert(funFact1)
            funFactDao.insert(funFact2)
            funFactDao.insert(funFact3)
            funFactDao.insert(funFact4)

            val movies = movieDao.getAll()
            val directors = directorDao.getAll()
            val directorMovies = directorMovieDao.getDirectorMovies()
            val movieFunFacts = movieFunFactDao.getMovieFunFacts()
            for (m in movies) {
                Log.i("CS3680", "Id: ${m.movieId} Movie: ${m.title}, release Date: ${m.releaseDate}")
            }
            for ( d in directors) {
                Log.i("CS3680", "Id: ${d.directorId} Name: ${d.name}")
            }
            for (dir in directorMovies) {
                Log.i("CS3680", "Director ${dir.director.name} has directed:")
                for (mov in dir.movies) {
                Log.i("CS3680", "${mov.title} ${mov.releaseDate}")
                }
            }

            for (mov in movieFunFacts) {
                Log.i("CS3680", "${mov.movie.title} fun facts!:")
                for (fac in mov.funFacts) {
                    Log.i("CS3680", fac.description)
                }
            }

        }
    }
}