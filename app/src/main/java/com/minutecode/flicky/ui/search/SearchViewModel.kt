package com.minutecode.flicky.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.minutecode.flicky.model.omdb.Movie
import com.minutecode.flicky.model.omdb.OmdbType
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint

class SearchViewModel : ViewModel() {

    private var _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    var text: LiveData<String> = _text

    fun httpBinGet() {
    }

    fun omdbSearch(title: String, type: OmdbType) {
            Fuel.request(OmdbEndpoint.SearchFor(title = title, type = type))
            .responseJson { _, _, result ->
                when(result) {
                    is Result.Failure -> {
                        Log.e("Search error", result.getException().localizedMessage ?: "No exception")
                    }
                    is Result.Success -> {
                        val json = result.value.obj()
                        val movieResultArray = json.getJSONArray("Search")
                        val movieArray: ArrayList<Movie> = ArrayList()
                        for (index in 0 until movieResultArray.length()) {
                            val movieResult = movieResultArray.getJSONObject(index)
                            movieArray.add(Movie(json = movieResult))
                        }
                        Log.d("JSON Object", json.toString(2))
                    }
                }
            }
    }


}