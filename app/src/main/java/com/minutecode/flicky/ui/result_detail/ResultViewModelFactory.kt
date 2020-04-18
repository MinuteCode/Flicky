package com.minutecode.flicky.ui.result_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minutecode.flicky.model.omdb.FullMovie
import com.minutecode.flicky.model.omdb.Movie

class ResultViewModelFactory(private val movie: Movie): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ResultDetailViewModel(movie = FullMovie(title = movie.title, year = movie.year, imdbId = movie.imdbId, type = movie.type, poster = movie.poster)) as T
}