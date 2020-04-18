package com.minutecode.flicky.ui.result_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.model.omdb.FullMovie
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.model.omdb.OmdbGenre
import com.minutecode.flicky.model.omdb.UserMovie
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint

class ResultDetailViewModel(val movie: Movie) : ViewModel() {
    private val TAG = "ResultDetailViewModel"

    private lateinit var listener: ResultDetailListener

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

    private var _movieGenres = MutableLiveData<Set<OmdbGenre>>().apply {
        value = setOf(OmdbGenre.adventure)
    }
    var movieGenre: LiveData<Set<OmdbGenre>> = _movieGenres

    fun setListener(listener: ResultDetailListener) {
        this.listener = listener
    }

    fun retrieveMovieDetail() {
        Fuel.request(OmdbEndpoint.DetailsFor(movie.imdbId))
            .responseJson { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.e(TAG, "Error getting info ${result.error.message}")
                        listener.detailRetrieveFailure(error = result.error)
                    }
                    is Result.Success -> {
                        val json = result.value.obj()
                        val fullMovie = FullMovie(json, movie)
                        _movieGenres.value = fullMovie.genre
                        _moviePlot.value = json.getString("Plot")
                        _moviePoster.value = json.getString("Poster")
                        listener.detailRetrieveSuccessful()
                    }
                }
            }
    }

    fun addResultToLibrary() {
        val userMovie = UserMovie("123456789", movie)
        Log.d(TAG, "Add movie to library $userMovie")
        Firebase.firestore
            .collection("Movies")
            .add(userMovie.asHashMap())
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${docRef.id}")
                listener.addToLibrarySuccessful()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                listener.addToLibraryFailure(e)
            }
    }
}

interface ResultDetailListener {
    fun detailRetrieveSuccessful()
    fun detailRetrieveFailure(error: FuelError)
    fun addToLibrarySuccessful()
    fun addToLibraryFailure(exception: Exception)
}
