package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.FavoriteAdapter
import com.example.mividplayer.databinding.FragmentFavoriteBinding
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.models.SongLayoutModel


class FavoriteFragment : Fragment() {


   companion object
   {
       lateinit var binding:FragmentFavoriteBinding
       var favoriteSongsList:ArrayList<SongLayoutModel> = ArrayList()
       @SuppressLint("StaticFieldLeak")
       lateinit var favoriteAdapter:FavoriteAdapter
   }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_favorite,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.favoriteHomeToolbar)
        setHasOptionsMenu(true)
        favoriteSongsList=SongLayoutModel.checkSongExist(favoriteSongsList)
        binding.favoriteHomeToolbar.title="Favorite Songs"
        favoriteAdapter= FavoriteAdapter(requireContext())
        favoriteAdapter.initFavoriteItemClickListener(object :FavoriteAdapter.FavoriteItemClickListener{
            override fun onItemClick(position: Int) {
                val send="FavoriteAdapter"

                findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment(position.toString()
                ,send))
            }

        })
        favoriteAdapter.setData(favoriteSongsList)
        binding.favoriteRecyclerView.adapter= favoriteAdapter
        binding.favoriteRecyclerView.setHasFixedSize(true)

        binding.favoriteRecyclerView.layoutManager=GridLayoutManager(requireContext(),3)
        binding.totalSongs.text="Total Songs ${favoriteSongsList.size}"
        if(favoriteSongsList.size<1)
        {
            binding.favoriteShuffle.visibility=View.GONE
        }
        binding.favoriteShuffle.setOnClickListener{
            if(favoriteSongsList.size>0)
                findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment("0","favoriteShuffle"))

        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }



}