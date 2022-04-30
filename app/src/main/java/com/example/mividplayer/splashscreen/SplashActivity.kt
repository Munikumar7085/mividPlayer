package com.example.mividplayer.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.example.mividplayer.MainActivity
import com.example.mividplayer.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


         setContentView(R.layout.activity_splash)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED


        val handler=Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        },1000)
    }
}