package com.example.myapplication.api.models

data class AddToWishlistResponse(
    val msg: String,
    val status: Int,
    val data: AddToWishlistResponseData?
)