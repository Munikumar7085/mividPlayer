package com.example.mividplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.mividplayer.databinding.ActivityMainBinding
import com.example.mividplayer.fragments.home.FavoriteFragment
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.fragments.startupslides.EnjoyMusicFragment
import com.example.mividplayer.fragments.startupslides.EnjoyMusicFragmentDirections
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.Playlist
import com.example.mividplayer.models.SongLayoutModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    companion object{
        val context=MainActivity

    }
    lateinit var binding:ActivityMainBinding
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("mainActivity1","ondestroy called")
        super.onCreate(savedInstanceState)
        requestExternalWritePermission()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val editor=getSharedPreferences("FAVORITES", Context.MODE_PRIVATE)
        val jsonString=editor.getString("favoritesmuni",null)
        val typeToken=object :TypeToken<ArrayList<SongLayoutModel>>(){}.type
       if(jsonString!=null)
        {
            val data:ArrayList<SongLayoutModel> =GsonBuilder().create().fromJson(jsonString,typeToken)
            FavoriteFragment.favoriteSongsList= ArrayList()
            FavoriteFragment.favoriteSongsList.addAll(data)
        }
          val jsonStringplay=editor.getString("playlist",null)
        val typeTokenPlay=object :TypeToken<ArrayList<Playlist>>(){}.type

         Log.i("mainac","jsonStringplay : ${jsonStringplay}")
        if(jsonStringplay!=null)
        {
            val playListData : ArrayList<Playlist> =GsonBuilder().create().fromJson(jsonStringplay,typeTokenPlay)
            MusicList.musicList=playListData

        }
        val getSkip=editor.getBoolean("skip",false)
        if(getSkip==true)
        {
            Navigation.findNavController(this,R.id.myNavHost).navigate(EnjoyMusicFragmentDirections.actionEnjoyMusicFragmentToMainViewFragment("0"))
        }


    }
    private fun requestExternalWritePermission()
    {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!grantResults.isEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show()
        }
        else
        {
            requestExternalWritePermission()
        }
    }

    override fun onStop() {
        val editor=getSharedPreferences("FAVORITES", Context.MODE_PRIVATE).edit()
        val jsonString=GsonBuilder().create().toJson(FavoriteFragment.favoriteSongsList)
        editor.putString("favoritesmuni",jsonString)

        val jsonStringPlaylist=GsonBuilder().create().toJson(MusicList.musicList)
        Log.i("mainca","jsonStringplay : ${jsonStringPlaylist}")
        editor.putString("playlist",jsonStringPlaylist)

        editor.putBoolean("skip",true)
        editor.apply()
        Log.i("mainActivity1","onStop called")
        super.onStop()
    }
    override fun onResume() {
        Log.i("mainActivity1","onResume called")

        super.onResume()
    }
    override fun onDestroy() {

        Log.i("mainActivity1","ondestroy called")
        super.onDestroy()
        if(!SongPlayingFragment.isplaying)
         SongLayoutModel.exitApplication()
    }
}