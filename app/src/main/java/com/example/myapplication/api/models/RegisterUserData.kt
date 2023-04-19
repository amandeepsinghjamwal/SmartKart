package com.example.myapplication.api.models

data class RegisterUserData(
    val emailId: String,
    val mobileNo: String,
    val name: String,
    val password: String
)