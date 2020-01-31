package com.minutecode.flicky.model.omdb

import org.json.JSONObject

data class Movie(val title: String, val year: Int, val imdbId: String, val type: OmdbType, val poster: String) {
    constructor(json: JSONObject) : this(
        title = json.getString("Title"),
        year = json.getString("Year").toInt(),
        imdbId = json.getString("imdbID"),
        type = OmdbType.valueOf(json.getString("Type")),
        poster = json.getString("Poster")
    )
}