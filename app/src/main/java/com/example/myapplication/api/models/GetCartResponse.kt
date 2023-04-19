package com.example.myapplication.api.models

data class GetCartResponse(
    val cartTotal: Double?,
    val `data`: List<CartResponseData>?,
    val msg: String,
    val status: Int?
)