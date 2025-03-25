package com.example.project3_version2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class DetailsActivity : AppCompatActivity() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var idTextView: TextView
    private lateinit var directorTextView: TextView
    private lateinit var returnButton: Button
    private lateinit var editTitle: EditText
    private lateinit var editReleaseDate: EditText
    private lateinit var editBoxOffice: EditText
    private lateinit var editDirector: Spinner
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details_page)

        appDatabase = AppDatabase.getDatabase(this)
        val movieDao = appDatabase.movieDao()
        val directorDao = appDatabase.directorDao()
        val movieRepository = MovieRepository(movieDao, directorDao)
        val movieFactory = MovieViewModelFactory(movieRepository)
        movieViewModel = ViewModelProvider(this, movieFactory)[MovieViewModel::class.java]

        val movieId = intent.getStringExtra("MOVIE_ID") ?: "0"
        val title = intent.getStringExtra("TITLE")
        val releaseDate = intent.getStringExtra("RELEASE_DATE")
        val boxOffice = intent.getStringExtra("BOX_OFFICE")
        val directorId = intent.getIntExtra("DIRECTOR_ID", 0)

        idTextView = findViewById(R.id.movieIdDisplay)

        editTitle = findViewById(R.id.editTitle)

        editReleaseDate = findViewById(R.id.editReleaseDate)

        editBoxOffice = findViewById(R.id.editBoxOffice)
        directorTextView = findViewById(R.id.directorDisplay)
        editDirector = findViewById(R.id.editDirector)
        updateButton = findViewById(R.id.updateBtn)
        returnButton = findViewById(R.id.return_btn)

        returnButton.setOnClickListener {
            returnToMain()
        }
        idTextView.setText(movieId)
        // Set the initial values in EditText fields
        editTitle.setText(title)
        editReleaseDate.setText(releaseDate)
        editBoxOffice.setText(boxOffice)

        // Fetch and display the director name based on directorId
        if (directorId != 0) {
            movieViewModel.fetchDirectorById(directorId)
        }

        movieViewModel.selectedDirector.observe(this) { director ->
            directorTextView.text = director?.name ?: "Unknown"
        }

        // Populate the director spinner
        movieViewModel.allDirectors.observe(this) { directors ->
            val directorNames = directors.map { it.name }
            val directorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, directorNames)
            directorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editDirector.adapter = directorAdapter

            // Set the spinner selection to the current director
            if (directorId != 0) {
                val currentDirector = directors.find { it.directorId == directorId }
                val currentDirectorName = currentDirector?.name ?: ""
                val directorPosition = directorNames.indexOf(currentDirectorName)
                if (directorPosition >= 0) {
                    editDirector.setSelection(directorPosition)
                }
            }
        }

        // Update movie on button click
        updateButton.setOnClickListener {
            val updatedTitle = editTitle.text.toString()
            val updatedReleaseDate = editReleaseDate.text.toString()
            val updatedBoxOffice = editBoxOffice.text.toString().toLong()

            // Get selected director ID from the spinner
            val selectedDirectorName = editDirector.selectedItem.toString()
            val selectedDirectorId = movieViewModel.getDirectorIdByName(selectedDirectorName)

            // Create updated movie object
            val updatedMovie = Movie(
                movieId = movieId.toInt(),
                title = updatedTitle,
                releaseDate = updatedReleaseDate,
                boxOffice = updatedBoxOffice,
                directorId = selectedDirectorId
            )

            // Update movie in the database
            movieViewModel.updateMovie(updatedMovie)
            finish()  // Close the activity after updating
        }
    }


    private fun returnToMain() {
        finish()
    }
}
