package com.example.myapplication.api.models

data class CartData(
    val userId: String,
    val cartId: String,
    val quantity: Int,
    val productCount:Int?,
    val productDetails: CartProductDetail?,

)