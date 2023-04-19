package com.example.myapplication.api.models

data class PlaceOrderResponseData(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val productCount: Int,
    val totalAmount: String,
    val updatedAt: String,
    val userId: String
)