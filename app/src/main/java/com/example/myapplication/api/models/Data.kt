package com.example.myapplication.api.models

data class Data(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val emailId: String,
    val fcmToken: String,
    val jwtToken: String?,
    val mobileNo: String,
    val name: String,
    val status: Int,
    val updatedAt: String
)