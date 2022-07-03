@file:Suppress("NAME_SHADOWING")

package com.example.mividplayer.fragments.music

import com.example.mividplayer.fragments.home.AlbumsFragment
import com.example.mividplayer.fragments.home.HomeFragment
import com.example.mividplayer.models.AlbumModel

class AlbumCreator {
    companion object{
        fun setAlbum() {
            var present: Boolean
            var presentIndex=0
            HomeFragment.songsCollection.forEachIndexed { _, song ->
                present=false

                AlbumsFragment.albumsList.forEachIndexed { index, album ->
                    if(album.albumName == song.albumName)
                    {
                        present=true
                        presentIndex=index
                    }

                }
                if(present)
                {
                    if(!AlbumsFragment.albumsList[presentIndex].albumSongs.contains(song))
                        AlbumsFragment.albumsList[presentIndex].albumSongs.add(song)
                }
                else
                {
                    val album= AlbumModel()
                    album.albumName=song.albumName
                    if(!album.albumSongs.contains(song))
                        album.albumSongs.add(song)
                    AlbumsFragment.albumsList.add(album)
                }
            }
            AlbumsFragment.isLayoutSet =true
        }
    }
}