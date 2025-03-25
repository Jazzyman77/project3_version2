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
    val allDirectors: LiveData<List<Director>> = repository.allDirectors.asLiveData()

    private val _directors = repository.allDirectors.asLiveData()
    val directors: LiveData<List<Director>> get() = _directors


    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> get() = _searchResults

    private val _selectedDirector = MutableLiveData<Director?>()
    val selectedDirector: LiveData<Director?> get() = _selectedDirector

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

    fun getNextMovieId(callback: (Int) -> Unit) {
        viewModelScope.launch {
            val maxId = repository.getMaxMovieId() ?: 0
            callback(maxId + 1)
        }
    }

    fun deleteAllMovie() {
        viewModelScope.launch {
            repository.deleteAllMovies()
        }

    }

    fun fetchDirectorById(directorId: Int) {
        viewModelScope.launch {
            _selectedDirector.value = repository.getDirectorById(directorId)
        }


    }
    fun getDirectorIdByName(name: String): Int {
        val director = allDirectors.value?.find { it.name == name }
        return director?.directorId ?: 0
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