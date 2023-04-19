package com.example.myapplication.api.models

data class WishlistResponse(
    val `data`: List<WishlistResponseData>?,
    val msg: String,
    val status: Int
)