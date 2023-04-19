package com.example.myapplication.api.models

data class ProductResponseData(
    val status: Int?,
    val staus: Int?,
    val msg: String,
    val totalProduct: Int?,
    val data: List<ProductDetails>
)