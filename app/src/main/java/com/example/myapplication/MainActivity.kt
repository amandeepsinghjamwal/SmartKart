package com.example.myapplication

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.entry.OtpVerificationActivity
import com.example.myapplication.home.HomeScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun changeScreen(jwtToken:String){
        intent= Intent(this, HomeScreen::class.java)
            .apply {
                putExtra("jwtToken",jwtToken)
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
    }
    fun gotoOtpScreen(userId:String,forgot:Boolean,email:String){
        intent= Intent(this,OtpVerificationActivity::class.java).apply {
            putExtra("userId",userId)
            putExtra("forgot",forgot)
            putExtra("email",email)
        }
        startActivity(intent)
    }

}