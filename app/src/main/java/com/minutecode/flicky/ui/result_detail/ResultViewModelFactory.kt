package com.minutecode.flicky.ui.result_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minutecode.flicky.model.omdb.Movie

class ResultViewModelFactory(private var movie: Movie): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ResultDetailViewModel(movie = movie) as T
}