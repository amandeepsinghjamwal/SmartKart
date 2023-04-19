package com.example.myapplication.api.models

data class CartResponseData(
    val _id: String?,
    val cartId: String?,
    val itemTotal: Double?,
    val productDetails: ProductDetails,
    val quantity: Int?,
    val userId: String?
)