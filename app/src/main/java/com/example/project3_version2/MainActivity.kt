package com.example.project3_version2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieRecyclerViewAdaptar: MovieRecyclerViewAdapter
    @SuppressLint("MissingInflatedId")
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
        val movieRepository = MovieRepository(movieDao)
        val movieFactory = MovieViewModelFactory(movieRepository)

        movieViewModel = ViewModelProvider(this, movieFactory)[MovieViewModel::class.java]

        movieRecyclerViewAdaptar = MovieRecyclerViewAdapter { movie ->
            movieViewModel.delete(movie) // Calls ViewModel delete function
        }

        val recyclerView: RecyclerView = findViewById(R.id.movie_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieRecyclerViewAdaptar

        // Observe LiveData from ViewModel and update adapter when data changes
        movieViewModel.allMovies.observe(this) { movies ->
            movieRecyclerViewAdaptar.submitList(movies)
        }

        val searchView: SearchView = findViewById(R.id.movieSearch)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    movieViewModel.allMovies.observe(this@MainActivity) { movies ->
                        movieRecyclerViewAdaptar.submitList(movies)
                    }
                } else {
                    movieViewModel.searchMovies(newText) // Calls suspend function
                    movieViewModel.searchResults.observe(this@MainActivity) { movies ->
                        movieRecyclerViewAdaptar.submitList(movies)
                    }
                }
                return true
            }
        })

    }

}