@file:Suppress("DEPRECATION")

package com.example.mividplayer.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.app.NotificationCompat
import com.example.mividplayer.MainActivity
import com.example.mividplayer.R
import com.example.mividplayer.application.MusicApplication
import com.example.mividplayer.fragments.playsong.NowPlayingFragment
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.models.SongLayoutModel
import com.example.mividplayer.receiver.MusicBroadcastReceiver


class MusicService:Service(), AudioManager.OnAudioFocusChangeListener{

    private val myBinder: IBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        val callback = object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT)

                return super.onMediaButtonEvent(mediaButtonIntent)
            }



            override fun onPause() {
                pause()
                super.onPause()
            }

            override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {

                super.onCommand(command, extras, cb)
            }
        }


        val mediaButtonReceiver = ComponentName(baseContext, MusicBroadcastReceiver::class.java)
        val tag="Music Service"
        mediaSession = MediaSessionCompat(
            baseContext,
            tag,  // Debugging tag, any string
            mediaButtonReceiver,
            null
        )
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.setCallback(callback) // a MediaSessionCompat.Callback


// This is what enables media buttons and should be called
// Immediately after getting audio focus

// This is what enables media buttons and should be called
// Immediately after getting audio focus
        mediaSession.isActive = true

        return myBinder

    }

    inner class MyBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseIcon: Int, playbackSpeed:Float) {
        val intent=Intent(baseContext,MainActivity::class.java)
        val contextIntent=PendingIntent.getActivity(this,0,intent,0)
        val prevIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playPauseIntent = Intent(
            baseContext,
            MusicBroadcastReceiver::class.java
        ).setAction(MusicApplication.PLAY_PAUSE)
        val playPausePendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextIntent =
            Intent(baseContext, MusicBroadcastReceiver::class.java).setAction(MusicApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val exitIntent =
            Intent(baseContext, MusicBroadcastReceiver::class.java).setAction(MusicApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt = SongPlayingFragment.songsList[SongPlayingFragment.index]
            .getImgArt(SongPlayingFragment.songsList[SongPlayingFragment.index].path)
        val img = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music)
        }
        val notification = NotificationCompat.Builder(baseContext, MusicApplication.CHANNEL_ID)
            .setContentIntent(contextIntent)
            .setContentTitle(SongPlayingFragment.songsList[SongPlayingFragment.index].songName)
            .setContentText(SongPlayingFragment.songsList[SongPlayingFragment.index].artist)
            .setSmallIcon(R.drawable.ic_small_music)
            .setLargeIcon(img)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous, "previous", prevPendingIntent)
            .addAction(playPauseIcon, "play", playPausePendingIntent)
            .addAction(R.drawable.ic_next, "next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "exit", exitPendingIntent)
            .build()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        {
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,mediaPlayer!!.duration.toLong())
                .build())
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer!!.currentPosition.toLong(),playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build())

        }

        startForeground(7, notification)


    }

    fun initializeMusic() {

        if (SongPlayingFragment.musicService != null) {
            if (SongPlayingFragment.musicService!!.mediaPlayer == null) {
                SongPlayingFragment.musicService!!.mediaPlayer = MediaPlayer()
            }
            SongPlayingFragment.musicService!!.mediaPlayer!!.reset()
            SongPlayingFragment.musicService!!.mediaPlayer!!.setDataSource(SongPlayingFragment.songsList[SongPlayingFragment.index].path)
            SongPlayingFragment.musicService!!.mediaPlayer!!.prepare()
            SongPlayingFragment.binding.currentPlayingStartTime.text= SongLayoutModel.getDuration(SongPlayingFragment.musicService!!.mediaPlayer!!.currentPosition.toLong())
            SongPlayingFragment.binding.currentSeekbar.progress=0
            SongPlayingFragment.binding.currentSeekbar.max= SongPlayingFragment.musicService!!.mediaPlayer!!.duration
            SongPlayingFragment.songId = SongPlayingFragment.songsList[SongPlayingFragment.index].id
        }

    }
    fun startRunnable()
    {
        runnable= Runnable {
            SongPlayingFragment.binding.currentPlayingStartTime.text= SongLayoutModel.getDuration(SongPlayingFragment.musicService!!.mediaPlayer!!.currentPosition.toLong())
            SongPlayingFragment.binding.currentSeekbar.progress=mediaPlayer!!.currentPosition

           Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange<=0)
        {
           pause()
        }
        else
        {
           play()
        }
    }
    private fun play()
    {
        SongPlayingFragment.isPlaying =true
        NowPlayingFragment.binding.pausePlay.setImageResource(R.drawable.ic_pause)
        changePlayPauseIcon(SongPlayingFragment.isPlaying)
        SongPlayingFragment.musicService!!.showNotification(R.drawable.ic_pause,1F)
        SongPlayingFragment.musicService!!.mediaPlayer!!.start()

    }
    fun pause()
    {
        SongPlayingFragment.isPlaying =false
        NowPlayingFragment.binding.pausePlay.setImageResource(R.drawable.ic_play_arrow)
        changePlayPauseIcon(SongPlayingFragment.isPlaying)
        SongPlayingFragment.musicService!!.showNotification(R.drawable.ic_play_arrow,0F)
        SongPlayingFragment.musicService!!.mediaPlayer!!.pause()
    }
    private fun changePlayPauseIcon(signal:Boolean)
    {
        if(signal)
        {
            SongPlayingFragment.binding.pauseBtn.visibility= View.GONE
            SongPlayingFragment.binding.playBtn.visibility= View.VISIBLE
        }
        else
        {
            SongPlayingFragment.binding.playBtn.visibility= View.GONE
            SongPlayingFragment.binding.pauseBtn.visibility= View.VISIBLE
        }
    }

}