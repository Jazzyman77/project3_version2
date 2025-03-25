package com.example.project3_version2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieRecyclerViewAdapter: MovieRecyclerViewAdapter
    private lateinit var movieTitle: EditText
    private lateinit var movieReleaseDate: EditText
    private lateinit var movieBoxOffice: EditText
    private lateinit var movieDirector: Spinner  // Corrected to Spinner
    private lateinit var clearButton: Button
    private lateinit var addButton: Button
    private lateinit var directorMovies: List<DirectorMovie>

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
        val movieDirectorDao = appDatabase.directorMovieDao()

        lifecycleScope.launch {
            try {
                directorMovies = movieDirectorDao.getDirectorMovies()

                for (directorMovie in directorMovies) {
                    Log.d("CS3680", "Director: ${directorMovie.director.name}") // Log the director's name
                    for (movie in directorMovie.movies) {
                        Log.d("CS3680", "\tMovie: ${movie.title} (${movie.releaseDate})") // Log each movie
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val movieRepository = MovieRepository(movieDao, directorDao)
        val movieFactory = MovieViewModelFactory(movieRepository)
        movieViewModel = ViewModelProvider(this, movieFactory)[MovieViewModel::class.java]

        // Initialize the adapter for RecyclerView
        movieRecyclerViewAdapter = MovieRecyclerViewAdapter(
            onItemClick = { movie ->  // onItemClick is now a lambda
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("MOVIE_ID", movie.movieId.toString())
                    putExtra("TITLE", movie.title)
                    putExtra("RELEASE_DATE", movie.releaseDate)
                    putExtra("BOX_OFFICE", movie.boxOffice.toString())
                    putExtra("DIRECTOR_ID", movie.directorId)
                }
                startActivity(intent)
            },
            onDelete = { movie ->
                movieViewModel.delete(movie)
            }
        )

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

      movieTitle = findViewById(R.id.titleInput)
      movieReleaseDate = findViewById(R.id.releaseDateInput)
      movieBoxOffice = findViewById(R.id.boxOfficeInput)
        addButton = findViewById(R.id.addBtn)
        addButton.setOnClickListener {
            movieViewModel.getNextMovieId { nextMovieId ->
                addMovie(nextMovieId)
            }
        }

        clearButton = findViewById(R.id.ClearMovieListBtn)
        clearButton.setOnClickListener {
        clearMovieList()
        }
    }

    fun addMovie(nextMovieId: Int) {
        // Get the selected director name from the spinner
        val selectedDirectorName = movieDirector.selectedItem.toString()

        // Check if a director is selected
        if (selectedDirectorName.isEmpty()) {
            // If no director is selected, show an error message
            Log.e("CS3680", "No director selected")
            return
        }

        // Find the director object from the list of directors
        val selectedDirector = movieViewModel.directors.value?.find { it.name == selectedDirectorName }

        // If no director is found, show an error message
        if (selectedDirector == null) {
            Log.e("CS3680", "Selected director not found")
            return
        }

        // Get the movie title, release date, and box office values
        val title = movieTitle.text.toString()
        val releaseDate = movieReleaseDate.text.toString()
        val boxOffice = movieBoxOffice.text.toString()

        // Check if any of the fields are blank
        if (title.isBlank()) {
            Log.e("CS3680", "Title cannot be blank")
            return
        }
        if (releaseDate.isBlank()) {
            Log.e("CS3680", "Release date cannot be blank")
            return
        }
        if (boxOffice.isBlank()) {
            Log.e("CS3680", "Box office cannot be blank")
            return
        }

        // If all checks pass, create the new movie object
        val directorId = selectedDirector.directorId
        val newMovie = Movie(movieId = nextMovieId, title =title, releaseDate = releaseDate, boxOffice = boxOffice.toString().toLong(), directorId = directorId)

        // Add the new movie to the database
        movieViewModel.insertMovie(newMovie)
        Log.i("CS3680", "Movie added with Director ID: $directorId")

        // Clear the input fields
        movieTitle.text.clear()
        movieReleaseDate.text.clear()
        movieBoxOffice.text.clear()

        // Reset the Spinner to the default (first item)
        movieDirector.setSelection(0)

        // Optionally, you could show a success message (Toast)
        Toast.makeText(this, "Movie added successfully!", Toast.LENGTH_SHORT).show()
    }

fun clearMovieList() {
    movieViewModel.deleteAllMovie()
}

}
