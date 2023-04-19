package com.example.myapplication.api.models

data class PlaceOrderResponse(
    val `data`: PlaceOrderResponseData,
    val msg: String,
    val status: Int
)