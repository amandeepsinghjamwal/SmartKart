package com.example.myapplication.api.models

data class AddtoCartResponse(
    val status: Int,
    val msg: String?,
    val data: CartData?
)