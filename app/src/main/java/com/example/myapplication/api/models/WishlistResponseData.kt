package com.example.myapplication.api.models

data class WishlistResponseData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val productDetails: ProductDetails,
    val updatedAt: String,
    val userId: String
)