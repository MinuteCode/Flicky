package com.minutecode.flicky.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.model.omdb.OmdbType
import com.minutecode.flicky.model.user.User
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint

class SearchViewModel : ViewModel() {
    private val TAG = "SearchViewModel"

    private var _text = MutableLiveData<String>().apply {
        value = "This is search Fragment"
    }
    var text: LiveData<String> = _text

    private var _searchResults = MutableLiveData<ArrayList<Movie>>().apply {
        value = arrayListOf()
    }
    var searchResults: LiveData<ArrayList<Movie>> = _searchResults

    private lateinit var searchListener: SearchListener

    fun omdbSearch(title: String, type: OmdbType) {
            Fuel.request(OmdbEndpoint.SearchFor(title = title, type = type))
            .responseJson { _, _, result ->
                when(result) {
                    is Result.Failure -> {
                        searchListener.searchFailure()
                        Log.e("Search error", result.getException().localizedMessage ?: "No exception")
                    }
                    is Result.Success -> {
                        val json = result.value.obj()
                        val movieResultArray = json.getJSONArray("Search")
                        val movieArray: ArrayList<Movie> = arrayListOf()
                        for (index in 0 until movieResultArray.length()) {
                            val movieResult = movieResultArray.getJSONObject(index)
                            movieArray.add(Movie(json = movieResult))
                        }
                        searchListener.searchSuccess(movieArray)
                        _searchResults.value = movieArray
                    }
                }
            }
    }

    fun getLoggedUser() {
        Firebase.firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .addSnapshotListener(object: EventListener<DocumentSnapshot> {
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    p0?.let {
                        if (it.exists()) {
                            val user = it.toObject(User::class.java)
                            Log.d(TAG, user.toString())
                        }
                    }
                }
            })
    }

    fun setSearchResults(to: ArrayList<Movie>) {
        _searchResults.value = to
    }

    fun setSearchListener(to: SearchListener) {
        searchListener = to
    }
}

interface SearchListener {
    fun searchSuccess(results: ArrayList<Movie>)
    fun searchFailure()
}