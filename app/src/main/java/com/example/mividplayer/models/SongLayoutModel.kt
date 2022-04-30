package com.example.mividplayer.models

import android.media.MediaMetadataRetriever
import com.example.mividplayer.fragments.home.FavoriteFragment
import com.example.mividplayer.fragments.playsong.NowPlayingFragment
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import java.io.File
import java.sql.Time
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class SongLayoutModel {
    var id :String=""
    var artist:String=""
    var songName:String=""
    var albumName:String=""
    var duration:Long=0
    var path:String=""
    var uri:String=""


    constructor()
    constructor(
        id: String,
        artist: String,
        songName: String,
        albumName: String,
        duration: Long,
        path: String,
        uri: String
    ) {
        this.id = id
        this.artist = artist
        this.songName = songName
        this.albumName = albumName
        this.duration = duration
        this.path = path
        this.uri = uri
    }


    fun getImgArt(path: String):ByteArray?
    {
        val retriver=MediaMetadataRetriever()
            retriver.setDataSource(path)
        return retriver.embeddedPicture

    }
    companion object
    {
        fun setposition(increment: Boolean) {


            if(increment)
            {
                SongPlayingFragment.index = if (SongPlayingFragment.index == SongPlayingFragment.songsList.size-1) 0 else SongPlayingFragment.index +1

            }
            else
            {
                SongPlayingFragment.index =if (SongPlayingFragment.index ==0) SongPlayingFragment.songsList.size-1 else SongPlayingFragment.index -1
            }
            NowPlayingFragment.listen.value=SongPlayingFragment.index
        }
        fun getduration(duration: Long):String
        {
            val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
            val seconds=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-(minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)))
            return String.format("%02d:%02d",minutes,seconds)
        }
        fun exitApplication()
        {
            if(SongPlayingFragment.musicService!=null)
            {
                SongPlayingFragment.musicService!!.audioManager.abandonAudioFocus (SongPlayingFragment.musicService)
                SongPlayingFragment.musicService!!.stopForeground(true)
                SongPlayingFragment.musicService!!.mediaPlayer!!.release()
                SongPlayingFragment.musicService=null

            }
            exitProcess(1)
        }
        fun isFavoriteChecker(id: String):Int
        {

            SongPlayingFragment.isfavorite=false
           FavoriteFragment.favoriteSongsList.forEachIndexed { index, songLayoutmodel ->
               if(id==songLayoutmodel.id)
               {
                   SongPlayingFragment.isfavorite=true
                   return index
               }
           }
            return -1
        }
        fun checkSongExist(songsList:ArrayList<SongLayoutModel>):ArrayList<SongLayoutModel>
        {
            songsList.forEachIndexed { index, songLayoutModel ->
                val file= File(songLayoutModel.path)
                if(!file.exists())
                {
                    songsList.removeAt(index)
                }
            }
            return songsList
        }
    }


}