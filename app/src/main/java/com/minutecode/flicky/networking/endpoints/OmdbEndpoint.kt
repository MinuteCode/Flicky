package com.minutecode.flicky.networking.endpoints

import com.github.kittinunf.fuel.core.HeaderValues
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.util.FuelRouting
import com.minutecode.flicky.model.omdb.OmdbType

sealed class OmdbEndpoint: FuelRouting {

    override val basePath: String = "https://www.omdbapi.com/?apikey=7a8a12b"

    class SearchFor(val title: String, val type: OmdbType): OmdbEndpoint()
    class DetailsFor(val imdbId: String): OmdbEndpoint()

    override val headers: Map<String, HeaderValues>?
        get() {
            return null
        }
    override val method: Method
        get() = Method.GET
    override val params: Parameters?
        get() {
            return when(this) {
                is SearchFor -> listOf("s" to title, "type" to type.type)
                is DetailsFor -> listOf("i" to imdbId, "plot" to "full")
            }
        }
    override val path: String
        get() = ""
    override val body: String?
        get() = null
    override val bytes: ByteArray?
        get() = null

    enum class OmdbValues(val value: String) {
        noValue("N/A")
    }
}