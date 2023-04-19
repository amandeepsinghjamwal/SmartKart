package com.example.myapplication.api.models

data class ResetPasswordData(
    val confirmPass: String,
    val newPass: String
)