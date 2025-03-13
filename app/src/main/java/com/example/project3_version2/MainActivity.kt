package com.example.project3_version2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieTitle: EditText
    private lateinit var movieReleaseDate: EditText
    private lateinit var movieBoxOffice: EditText
    private lateinit var movieDirector: Spinner  // Corrected to Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize appDatabase correctly
        appDatabase = AppDatabase.getDatabase(this)

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the database, DAO, and repository
        val movieDao = appDatabase.movieDao()
        val directorDao = appDatabase.directorDao()
        val movieRepository = MovieRepository(movieDao, directorDao)
        val movieFactory = MovieViewModelFactory(movieRepository)
        movieViewModel = ViewModelProvider(this, movieFactory)[MovieViewModel::class.java]

        // Initialize the adapter for RecyclerView
        movieRecyclerViewAdapter = MovieRecyclerViewAdapter { movie ->
            movieViewModel.delete(movie) // Calls ViewModel delete function
        }

        val recyclerView: RecyclerView = findViewById(R.id.movie_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieRecyclerViewAdapter

        // Observe LiveData from ViewModel and update adapter when data changes
        movieViewModel.allMovies.observe(this) { movies ->
            movieRecyclerViewAdapter.submitList(movies)
        }

        // Set up the search functionality
        val searchView: androidx.appcompat.widget.SearchView = findViewById(R.id.movieSearch)  // Make sure it's the correct SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    movieViewModel.searchResults.observe(this@MainActivity) { movies ->
                        movieRecyclerViewAdapter.submitList(movies)
                    }
                } else {
                    movieViewModel.searchMovies(newText)  // Calls suspend function
                }
                return true
            }
        })

        // Set up the director Spinner (you need to observe and update it here)
        movieDirector = findViewById(R.id.spinner)  // Make sure the spinner is initialized here.
        movieViewModel.directors.observe(this) { directors ->
            val directorNames = directors.map { it.name }  // Assuming 'name' is a field in Director
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, directorNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            movieDirector.adapter = adapter
        }
    }
}
