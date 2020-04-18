package com.minutecode.flicky.model.omdb

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

open class Movie(
    open val title: String,
    open val year: Int,
    open val imdbId: String,
    open val type: OmdbType,
    open val poster: String): Parcelable {

    constructor(parcel: Parcel) : this(
        title = parcel.readString() ?: "NULL",
        year = parcel.readInt(),
        imdbId = parcel.readString() ?: "NULL",
        type = OmdbType.movie,
        poster = parcel.readString() ?: "NULL"
    )

    constructor(json: JSONObject) : this(
        title = json.getString("Title"),
        year = json.getString("Year").toInt(),
        imdbId = json.getString("imdbID"),
        type = OmdbType.valueOf(json.getString("Type")),
        poster = json.getString("Poster")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(year)
        parcel.writeString(imdbId)
        parcel.writeString(poster)
    }

    open fun asHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "title" to title,
            "year" to year,
            "imdbId" to imdbId,
            "type" to type,
            "poster" to poster
        )
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}