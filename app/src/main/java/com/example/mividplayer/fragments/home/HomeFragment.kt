package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toLowerCase
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentHomeBinding
import com.example.mividplayer.models.SongLayoutModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    companion object{
        lateinit var songsCollection:ArrayList<SongLayoutModel>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        songsCollection =getsongs()
        binding.iconHistory.setOnClickListener{
            Toast.makeText(requireContext(),"You clicked history",Toast.LENGTH_SHORT).show()
        }

        binding.homeShuffle.setOnClickListener{
            val fm = fragmentManager


            for (entry in 0 until fm!!.backStackEntryCount) {
                Log.i( "SongPlaying","Found fragment at homefragment: " + fm.getBackStackEntryAt(entry).javaClass)
            }
            if(songsCollection.size>0)
            findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment("0","Homeshuffle"))
            else
                Toast.makeText(requireContext(),"No songs to play",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    @SuppressLint("Range")
    fun getsongs():ArrayList<SongLayoutModel>
    {
        var songsList=ArrayList<SongLayoutModel>()

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
                    val image_uri= Uri.withAppendedPath(uri,albumId.toString()).toString()
                    val song= SongLayoutModel(id,artist,title,album,duration,path,image_uri)
                    val file= File(path)
                    if(file.exists()&&duration>=6000&& !album.lowercase(Locale.getDefault()).equals("whatsapp audio"))
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