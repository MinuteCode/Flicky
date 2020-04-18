package com.minutecode.flicky.model.omdb

import android.annotation.SuppressLint
import com.minutecode.flicky.networking.endpoints.OmdbEndpoint
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


open class FullMovie(
    override val title: String,
    override val year: Int,
    override val imdbId: String,
    override val type: OmdbType,
    override val poster: String) : Movie(title, year, imdbId, type, poster) {
    var genre: Set<String> = setOf() //TODO: Map the genres to an OmdbGenre
        get() = field
    var rated: String = ""
        get() = field
    var releaseDate: Date? = null
        get() = field
    var runtime: Int = 0
        get() = field
    var director: String = ""
        get() = field
    var writer: String = ""
        get() = field
    var actors: List<String> = arrayListOf()
        get() = field
    var plot: String = ""
        get() = field
    var language: List<String> = arrayListOf()
        get() = field
    var country: Set<String> = setOf()
        get() = field
    var awards: String = ""
        get() = field
    var ratings: List<Pair<String, String>> = arrayListOf()
        get() = field
    var metascore: Int? = null
        get() = field
    var imdbRating: Float = 0f
        get() = field
    var imdbVotes: Int = 0
        get() = field
    var dvdReleaseDate: Date? = null
        get() = field
    var boxOffice: Double = 0.0
        get() = field
    var production: String = ""
        get() = field
    var website: String = ""
        get() = field

    constructor(genre: Set<String>,
                rated: String,
                releaseDate: Date?,
                runtime: Int,
                director: String,
                writer: String,
                actors: List<String>,
                plot: String,
                language: List<String>,
                country: Set<String>,
                awards: String,
                ratings: List<Pair<String, String>>,
                metascore: Int?,
                imdbRating: Float,
                imdbVotes: Int,
                dvdReleaseDate: Date?,
                boxOffice: Double,
                production: String,
                website: String,
                movie: Movie) : this(movie.title, movie.year, movie.imdbId, movie.type, movie.poster) {
        this.genre = genre
        this.rated = rated
        this.releaseDate = releaseDate
        this.runtime = runtime
        this.director = director
        this.writer = writer
        this.actors = actors
        this.plot = plot
        this.language = language
        this.country = country
        this.awards = awards
        this.ratings = ratings
        this.metascore = metascore
        this.imdbRating = imdbRating
        this.imdbVotes = imdbVotes
        this.dvdReleaseDate = dvdReleaseDate
        this.boxOffice = boxOffice
        this.production = production
        this.website = website
    }

    @SuppressLint("SimpleDateFormat")
    constructor(json: JSONObject, movie: Movie) : this(
        genre = HashSet<String>(json.getString("Genre").split(",")),
        rated = json.getString("Rated"),
        releaseDate = SimpleDateFormat("dd MMM YYYY").parse(json.getString("Released")),
        runtime = json.getString("Runtime").replace(" min", "").toInt(),
        director = json.getString("Director"),
        writer = json.getString("Writer"),
        actors = json.getString("Actors").split(",").onEach {
            if (it.startsWith(" ")) { it.drop(1) }
        },
        plot = json.getString("Plot"),
        language = json.getString("Language").split(","),
        country = json.getString("Country").split(",").let {
            val countries: MutableSet<String> = mutableSetOf()
            for (country: String in it) { countries.add(country) }
            countries
        },
        awards = json.getString("Awards"),
        ratings = json.getJSONArray("Ratings").let { jsonArray: JSONArray ->
            val ratings: ArrayList<Pair<String, String>> = arrayListOf()
            for (index in 0 until jsonArray.length()) {
                val ratingPair = Pair<String, String>(
                    jsonArray.getJSONObject(index).getString("Source"),
                    jsonArray.getJSONObject(index).getString("Value")
                )
                ratings.add(ratingPair)
            }
            ratings
        },
        metascore = json.getString("Metascore").let {
            if (it == OmdbEndpoint.OmdbValues.noValue.value) { null }
            else { it.toInt() }
        },
        imdbRating = json.getString("imdbRating").toFloat(),
        imdbVotes = json.getString("imdbVotes").replace(",", "").toInt(),
        dvdReleaseDate = json.getString("DVD").let {
            if (it == OmdbEndpoint.OmdbValues.noValue.value) { null }
            else { SimpleDateFormat("dd MMM YYYY").parse(json.getString("DVD")) }
        },
        boxOffice = json.getString("BoxOffice").let {
            if (it == "N/A") { 0.0 }
            else { it.replace(",","").replace("$", "").toDouble() }
        },
        production = json.getString("Production"),
        website = json.getString("Website"),
        movie = movie
    )

    override fun asHashMap(): HashMap<String, Any> {
        val hashMap = super.asHashMap()
        hashMap["genre"] = ArrayList(genre)
        hashMap["rated"] = rated
        hashMap["releaseDate"] = releaseDate ?: ""
        hashMap["runtime"] = runtime
        hashMap["director"] = director
        hashMap["writer"] = writer
        hashMap["actors"] = actors
        hashMap["plot"] = plot
        hashMap["language"] = language
        hashMap["country"] = ArrayList(country)
        hashMap["awards"] = awards
        hashMap["ratings"] = ratings
        hashMap["metascore"] = metascore ?: ""
        hashMap["imdbRating"] = imdbRating
        hashMap["imdbVotes"] = imdbVotes
        hashMap["dvdReleaseDate"] = dvdReleaseDate ?: ""
        hashMap["boxOffice"] = boxOffice
        hashMap["production"] = production
        hashMap["website"] = website
        return hashMap
    }
}