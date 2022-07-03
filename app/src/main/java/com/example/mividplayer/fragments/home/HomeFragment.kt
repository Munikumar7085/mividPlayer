@file:Suppress("DEPRECATION")

package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentHomeBinding
import com.example.mividplayer.fragments.music.AlbumCreator
import com.example.mividplayer.models.SongLayoutModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    companion object{
        lateinit var songsCollection:ArrayList<SongLayoutModel>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
        binding.homeToolbar.title="Mivid Player"
        songsCollection =getsongs()
        val sdf=SimpleDateFormat("HH")
        val c=getGreeting(Integer.parseInt(sdf.format(Date())))
        binding.iconHistory.setOnClickListener{
            findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToHistoryFragment())
        }

        if(!AlbumsFragment.isLayoutSet)
        {
            AlbumCreator.setAlbum()
        }
        binding.wishing.text= c
        binding.homeShuffle.setOnClickListener{
            if(songsCollection.size>0)
            findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment("0","Homeshuffle"))
            else
                Toast.makeText(requireContext(),"No songs to play",Toast.LENGTH_SHORT).show()
        }
        val randomAlbum=(1..AlbumsFragment.albumsList.size).random()-1
        val albumModel= AlbumsFragment.albumsList[(randomAlbum)]
        Glide.with(requireContext()).load(albumModel.albumSongs[0].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music).centerCrop())
            .into(binding.randomSuggestion)
        binding.gotoAlbum.setOnClickListener{
            findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToAlbumsListFragment(randomAlbum.toString()))
        }
        return binding.root
    }

    private fun getGreeting(hourOfDay: Int): String {
        if(hourOfDay in 16..20)
        {
            return "Good Evening"
        }
        if(hourOfDay in 21..23)
        {
            return "Good Night"
        }
        if(hourOfDay in 12..15)
        {
            return "Good Afternoon"
        }
        return "GoodMorning"
    }

    @SuppressLint("Range")
    fun getsongs():ArrayList<SongLayoutModel>
    {
        val songsList=ArrayList<SongLayoutModel>()

        val selection= MediaStore.Audio.Media.IS_MUSIC+"!=0"
        val projection= arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA)
        val cursor=requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,selection,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER,null)
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                do {
                    val id=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val title=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artist=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumId=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val imageUri: String = Uri.withAppendedPath(uri,albumId.toString()).toString()
                    val song= SongLayoutModel(id,artist,title,album,duration,path,imageUri)
                    val file= File(path)
                    if(file.exists()&&duration>=6000&& album.lowercase(Locale.getDefault()) != "whatsapp audio")
                    {
                        songsList.add(song)
                    }


                }while (cursor.moveToNext())
                cursor.close()
            }
        }


        return songsList
    }

}