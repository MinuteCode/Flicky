package com.minutecode.flicky.model.omdb

class UserMovie(
    override val title: String,
    override val year: Int,
    override val imdbId: String,
    override val type: OmdbType,
    override val poster: String) : FullMovie(title, year, imdbId, type, poster) {
    var userId: String = ""

    constructor(userId: String, movie: Movie) : this(movie.title, movie.year, movie.imdbId, movie.type, movie.poster) {
        this.userId = userId
    }

    override fun asHashMap(): HashMap<String, Any> {
        val movieHashMap = super.asHashMap()
        movieHashMap["userId"] = userId
        return movieHashMap
    }
}