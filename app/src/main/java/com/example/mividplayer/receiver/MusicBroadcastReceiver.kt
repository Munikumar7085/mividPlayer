package com.example.mividplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.application.MusicApplication
import com.example.mividplayer.fragments.playsong.NowPlayingFragment
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.fragments.playsong.SongPlayingFragment.Companion.binding
import com.example.mividplayer.models.SongLayoutModel

class MusicBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null)
        {
            Glide.with(context).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
                .apply(RequestOptions.placeholderOf(R.drawable.music))
                .into(NowPlayingFragment.binding.songImg)
        }
        when(intent?.action)
        {
            MusicApplication.PREVIOUS->
            {
                nextPrev(false,context!!)
            }

            MusicApplication.PLAY_PAUSE-> {
                if(SongPlayingFragment.isPlaying)
                {
                    pauseMusic()
                }
                else
                {
                    playMusic()
                }
            }
            MusicApplication.NEXT->{
                nextPrev(true,context!!)
            }
            MusicApplication.EXIT->
            {
                SongLayoutModel.exitApplication()
            }

        }


    }

    private fun changePlayPauseIcon(signal:Boolean)
    {
        if(signal)
        {
            binding.pauseBtn.visibility=View.GONE
            binding.playBtn.visibility=View.VISIBLE
        }
        else
        {
            binding.playBtn.visibility=View.GONE
            binding.pauseBtn.visibility=View.VISIBLE
        }
    }
    private fun pauseMusic() {
        SongPlayingFragment.isPlaying=false
        SongPlayingFragment.musicService!!.mediaPlayer!!.pause()
        SongPlayingFragment.musicService!!.showNotification(R.drawable.ic_play_arrow,0F)
        changePlayPauseIcon(false)
        NowPlayingFragment.binding.pausePlay.setImageResource(R.drawable.ic_play_arrow)

    }
    private fun playMusic()

    {
        SongPlayingFragment.isPlaying=true
        SongPlayingFragment.musicService!!.mediaPlayer!!.start()
        SongPlayingFragment.musicService!!.showNotification(R.drawable.ic_pause,1F)
        changePlayPauseIcon(true)
        NowPlayingFragment.binding.pausePlay.setImageResource(R.drawable.ic_pause)

    }
    private fun nextPrev(increment:Boolean, context: Context)
    {
        SongLayoutModel.setPosition(increment)
        changePlayPauseIcon(SongPlayingFragment.isPlaying)
        SongPlayingFragment.musicService!!.initializeMusic()
        Glide.with(context).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(binding.currentPlayingImg)
        Glide.with(context).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(NowPlayingFragment.binding.songImg)
        NowPlayingFragment.binding.nowPlayingSong.text=SongPlayingFragment.songsList[SongPlayingFragment.index].songName
        binding.currentPlayingSongName.text = SongPlayingFragment.songsList[SongPlayingFragment.index].songName
        binding.currentPlayingSingerName.text = SongPlayingFragment.songsList[SongPlayingFragment.index].artist
        binding.currentPlayingEndTime.text = SongLayoutModel
            .getDuration(SongPlayingFragment.songsList[SongPlayingFragment.index].duration)
        SongPlayingFragment.favIndex=SongLayoutModel.isFavoriteChecker(SongPlayingFragment.songsList[SongPlayingFragment.index].id)
        changeFavIcon()

       playMusic()


    }
    private fun changeFavIcon() {
        if(SongPlayingFragment.isFavorite)
        {
            binding.favoriteOff.visibility=View.GONE
            binding.favoriteOn.visibility=View.VISIBLE
        }
        else
        {
            binding.favoriteOn.visibility=View.GONE
            binding.favoriteOff.visibility=View.VISIBLE
        }
    }
}