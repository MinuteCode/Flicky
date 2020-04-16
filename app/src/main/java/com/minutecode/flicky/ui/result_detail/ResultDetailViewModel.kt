package com.minutecode.flicky.ui.result_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint

class ResultDetailViewModel(val movie: Movie) : ViewModel() {
    private var _movieTitle = MutableLiveData<String>().apply {
        value = movie.title
    }
    var movieTitle: LiveData<String> = _movieTitle

    private var _movieYear = MutableLiveData<Int>().apply {
        value = movie.year
    }
    var movieYear: LiveData<Int> = _movieYear

    private var _moviePoster = MutableLiveData<String>().apply {
        value = movie.poster
    }
    var moviePoster: LiveData<String> = _moviePoster

    private var _moviePlot = MutableLiveData<String>().apply {
        value = "Movie Plot"
    }
    var moviePlot: LiveData<String> = _moviePlot

    fun retrieveMovieDetail() {
        Fuel.request(OmdbEndpoint.DetailsFor(movie.imdbId))
            .responseJson { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.e("ResultDetailViewModel", "Error getting info")
                    }
                    is Result.Success -> {
                        val json = result.value.obj()
                        _moviePlot.value = json.getString("Plot")
                        _moviePoster.value = json.getString("Poster")
                    }
                }
            }
    }
}
