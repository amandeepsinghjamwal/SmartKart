package com.example.myapplication.api.models

data class PlaceOrderRequestData(
    val cartId: String,
    val cartTotal: String
)