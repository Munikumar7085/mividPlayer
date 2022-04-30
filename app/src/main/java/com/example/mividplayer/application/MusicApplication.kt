package com.example.mividplayer.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.provider.Settings

class MusicApplication:Application() {
    companion object{
        const val CHANNEL_ID="channelId"
        const val PLAY_PAUSE="playPause"
        const val NEXT="next"
        const val PREVIOUS="previous"
        const val EXIT="exit"

    }
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            val notificationChannel=NotificationChannel(CHANNEL_ID,"Now playing",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description="You are listening the nice song"
            val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}