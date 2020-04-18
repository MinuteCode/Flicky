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

    constructor(userId: String, fullMovie: FullMovie): this(fullMovie.title, fullMovie.year, fullMovie.imdbId, fullMovie.type, fullMovie.poster) {
        genre = fullMovie.genre
        rated = fullMovie.rated
        releaseDate = fullMovie.releaseDate
        runtime = fullMovie.runtime
        director = fullMovie.director
        writer = fullMovie.writer
        actors = fullMovie.actors
        plot = fullMovie.plot
        language = fullMovie.language
        country = fullMovie.country
        awards = fullMovie.awards
        ratings = fullMovie.ratings
        metascore = fullMovie.metascore
        imdbRating = fullMovie.imdbRating
        dvdReleaseDate = fullMovie.dvdReleaseDate
        boxOffice = fullMovie.boxOffice
        imdbVotes = fullMovie.imdbVotes
        production = fullMovie.production
        website = fullMovie.website
        this.userId = userId
    }

    override fun asHashMap(): HashMap<String, Any> {
        val movieHashMap = super.asHashMap()
        movieHashMap["userId"] = userId
        return movieHashMap
    }
}