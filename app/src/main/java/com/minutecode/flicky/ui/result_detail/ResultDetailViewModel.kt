package com.minutecode.flicky.ui.result_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.model.omdb.FullMovie
import com.minutecode.flicky.model.omdb.UserMovie
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint

class ResultDetailViewModel(var movie: FullMovie) : ViewModel() {
    private val TAG = "ResultDetailViewModel"

    private lateinit var listener: ResultDetailListener

    private var firestore = Firebase.firestore
    private lateinit var userDocumentPath: String

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

    private var _movieGenres = MutableLiveData<List<String>>().apply {
        value = listOf("")
    }
    var movieGenre: LiveData<List<String>> = _movieGenres

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
                        movie = FullMovie(json, movie)
                        _movieGenres.value = movie.genre
                        _moviePlot.value = json.getString("Plot")
                        _moviePoster.value = json.getString("Poster")
                        listener.detailRetrieveSuccessful()
                    }
                }
            }
    }

    fun retrieveUserDocPath() {
        firestore.collection("Users")
            .whereEqualTo("authId", FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener { query ->
                userDocumentPath = query.documents[0].reference.path
            }
    }

    fun addResultToLibrary() {
        if (canAddMovieToLibrary()) {
            val userMovie = UserMovie(FirebaseAuth.getInstance().currentUser!!.uid, movie)
            Log.d(TAG, "Add movie to library $userMovie")
            firestore
                .collection("Users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("movies", FieldValue.arrayUnion(movie.asHashMap()))
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added with ID: ")
                    listener.addToLibrarySuccessful()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                    listener.addToLibraryFailure(e)
                }
        }
    }

    private fun canAddMovieToLibrary(): Boolean {
        var canAdd = true
        firestore.collection("Users")
            .whereEqualTo("authId", FirebaseAuth.getInstance().currentUser!!.uid)
            .whereArrayContains("movies", movie.asHashMap())
            .get()
            .addOnSuccessListener { query ->
                canAdd = query.documents.isEmpty()
            }
        return canAdd
    }
}

interface ResultDetailListener {
    fun detailRetrieveSuccessful()
    fun detailRetrieveFailure(error: FuelError)
    fun addToLibrarySuccessful()
    fun addToLibraryFailure(exception: Exception)
}
