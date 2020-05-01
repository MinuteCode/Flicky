package com.minutecode.flicky.model.omdb

data class Rating(var source: String, var value: String) {
    constructor() : this("", "")
}