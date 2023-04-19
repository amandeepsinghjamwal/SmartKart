package com.example.myapplication.entry

import android.annotation.SuppressLint
import android.content.Intent
import kotlin.concurrent.schedule
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.ApplicationClass
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.home.HomeScreen
import kotlinx.coroutines.*
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        intent= Intent(this,HomeScreen::class.java).apply{
            flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val intent2= Intent(this,MainActivity::class.java).apply{
            flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if(ApplicationClass.sharedPreferences!!.contains("JWTtoken")) {
            Timer("SettingUp", false).schedule(1500) {
                startActivity(intent)
            }
        }
        else{
            Timer("SettingUp", false).schedule(1500) {
                    startActivity(intent2)
            }
        }
    }
}