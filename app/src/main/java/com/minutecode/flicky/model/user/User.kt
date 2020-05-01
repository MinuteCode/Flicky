package com.minutecode.flicky.model.user

import com.minutecode.flicky.model.omdb.FullMovie

data class User(
    var id: String?,
    var movies: List<FullMovie>,
    var displayName: String?,
    var email: String?,
    var phoneNumber: String?
) {

    constructor(): this("", listOf(), null, null, null)

    val storeFormat: HashMap<String, Any?> get() {
        return hashMapOf(
            "authId" to id,
            "movies" to movies,
            "displayName" to displayName,
            "email" to email,
            "phoneNumber" to phoneNumber
        )
    }
}