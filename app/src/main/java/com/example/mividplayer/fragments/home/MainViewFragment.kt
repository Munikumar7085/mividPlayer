package com.example.mividplayer.fragments.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.mividplayer.R
import com.example.mividplayer.adapters.ViewPagerAdapter
import com.example.mividplayer.databinding.FragmentMainViewBinding
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.SongLayoutModel
import com.google.gson.GsonBuilder


class MainViewFragment : Fragment() {

    private var onpagechanger: OnPageChangeCallback?=null
    private val myargs by navArgs<MainViewFragmentArgs>()
    private lateinit var binding:FragmentMainViewBinding

    companion object{
        var fragmentIndex=0
        var isfirst=true


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_main_view,container,false)
        fragmentIndex=myargs.fragment.toInt()


        val viewPager_id_list= intArrayOf(R.id.nav_home,
                                          R.id.nav_songs,
                                          R.id.nav_Favorite,
                                          R.id.nav_albums,
                                          R.id.nav_playlist)

            val fragmentList:ArrayList<Fragment> = ArrayList(arrayListOf(
                HomeFragment(),SongsFragment(),FavoriteFragment(),AlbumsFragment(),PlaylistFragment()
            ))


        val viewAdapter=ViewPagerAdapter(childFragmentManager,lifecycle,fragmentList)
        binding.myViewPager.adapter=viewAdapter

        if(isfirst)
        {
            binding.myViewPager.currentItem=3
            binding.myChipNavigation.setItemSelected(viewPager_id_list[3])
            binding.myViewPager.currentItem=0
            isfirst=false
        }


        binding.myChipNavigation.setOnItemSelectedListener {id->

            when(id)
            {

                R.id.nav_home ->
                {
                    Log.i("MainView","nav_home selected")
                    binding.myViewPager.currentItem=0

                }
                R.id.nav_songs ->
                {
                    Log.i("MainView","nav_songs selected")
                    binding.myViewPager.currentItem=1

                }
                R.id.nav_Favorite ->
                {
                    Log.i("MainView","nav_favorites selected")
                    binding.myViewPager.currentItem=2

                }
                R.id.nav_albums ->
                {

                    Log.i("MainView","nav_albums selected")
                    binding.myViewPager.currentItem=3

                }

                R.id.nav_playlist ->
                {
                    Log.i("MainView","nav_playlist selected")
                    binding.myViewPager.currentItem=4

                }
            }
        }

        binding.myChipNavigation.setItemSelected(viewPager_id_list[fragmentIndex],true)

        onpagechanger=object :OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {

                binding.myChipNavigation.setItemSelected(viewPager_id_list[position])
                Log.i("MainView","${viewPager_id_list[position]} selected")
              }
        }
        if(onpagechanger!=null)
            binding.myViewPager.registerOnPageChangeCallback(onpagechanger as OnPageChangeCallback)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if(binding.myViewPager.currentItem==0)
                {

                    val alretbuilder= context?.let { AlertDialog.Builder(it) }
                    if (alretbuilder != null) {
                        alretbuilder.setTitle("Exit")
                            .setMessage("Do you want to close app?")
                            .setPositiveButton("Yes"){_,_->
                                val editor=(activity as AppCompatActivity).getSharedPreferences("FAVORITES", Context.MODE_PRIVATE).edit()
                                val jsonString=GsonBuilder().create().toJson(FavoriteFragment.favoriteSongsList)
                                editor.putString("favoritesmuni",jsonString)

                                val jsonStringPlaylist=GsonBuilder().create().toJson(MusicList.musicList)
                                Log.i("mainca","jsonStringplay : ${jsonStringPlaylist}")
                                editor.putString("playlist",jsonStringPlaylist)
                                editor.apply()

                                SongLayoutModel.exitApplication()
                            }
                            .setNegativeButton("No"){dialog,_->
                                dialog.dismiss()
                            }
                    }
                    val dialog= alretbuilder?.create()
                    if (dialog != null) {
                        dialog.show()
                    }


                }
                else
                {

                    binding.myChipNavigation.setItemSelected(viewPager_id_list[0])
                    binding.myViewPager.currentItem=0
                }

            }
        })
        return binding.root
    }

    override fun onDestroy() {
        if(onpagechanger!=null)
        binding.myViewPager.unregisterOnPageChangeCallback(onpagechanger as OnPageChangeCallback)
        super.onDestroy()
    }




}