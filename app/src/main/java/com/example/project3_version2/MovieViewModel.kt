package com.example.project3_version2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {
    val allMovies: LiveData<List<Movie>> = repository.allMovies.asLiveData()


    private val _directors = repository.allDirectors.asLiveData()
    val directors: LiveData<List<Director>> get() = _directors


    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> get() = _searchResults


    fun insertMovie(movie: Movie) = viewModelScope.launch {
        repository.insertMovie(movie)
    }

    fun updateMovie(movie: Movie) = viewModelScope.launch {
        repository.updateMovie(movie)
    }



    fun delete(movie: Movie) = viewModelScope.launch {
        repository.deleteMovie(movie)

    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchMovies(query) // Runs in coroutine
        }
    }
}

class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}