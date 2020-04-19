package com.minutecode.flicky.model.user

import com.minutecode.flicky.model.omdb.FullMovie

class User(
    private val id: String,
    private val movies: Set<FullMovie>,
    private val displayName: String?,
    private val email: String?,
    private val phoneNumber: String?
) {

    constructor(): this("", setOf(), null, null, null)

    val storeFormat: HashMap<String, Any?> get() {
        return hashMapOf(
            "authId" to id,
            "movies" to ArrayList(movies),
            "displayName" to displayName,
            "email" to email,
            "phoneNumber" to phoneNumber
        )
    }
}