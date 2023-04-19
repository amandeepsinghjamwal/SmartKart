package com.example.myapplication.api.models

data class IncrementResponseData(
    val `data`: IncData?,
    val msg: String,
    val status: Int
)