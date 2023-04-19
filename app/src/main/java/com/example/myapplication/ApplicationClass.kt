package com.example.myapplication

import android.app.Application
import android.content.SharedPreferences

class ApplicationClass: Application() {

    companion object{
        var sharedPreferences:SharedPreferences?=null
        var editor:SharedPreferences.Editor?=null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
    }
}