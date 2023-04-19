package com.example.myapplication.api.models

data class OrderHistoryResponse(
    val `data`: List<OrderHistoryProductsData>,
    val msg: String,
    val status: Int
)