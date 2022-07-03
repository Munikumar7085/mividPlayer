package com.example.mividplayer.fragments.music

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.SongsAdapter
import com.example.mividplayer.databinding.FragmentSelectionBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.fragments.home.HomeFragment
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.SongLayoutModel


@Suppress("DEPRECATION")
class SelectionFragment : Fragment() {


    private val selectionArgs by navArgs<SelectionFragmentArgs>()
    lateinit var binding:FragmentSelectionBinding
    lateinit var adapter:SongsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_selection,container,false)
        val position=selectionArgs.position
        adapter= SongsAdapter(requireContext())
        adapter.setsData(HomeFragment.songsCollection)
        adapter.initItemClickListener(object : SongsAdapter.onItemClickListener{


            override fun onitemClick(position: Int, view: SongLayoutBinding) {
              if(checkSong(HomeFragment.songsCollection[position]))
              {


                  view.root.setBackgroundColor(resources.getColor(R.color.selection))

                  Log.i("music","called for ${HomeFragment.songsCollection[position].songName}")
              }
                else
              {
                  view.root.setBackgroundColor(resources.getColor(R.color.Theme_bg))
              }
            }

        })

        binding.selectionRecyclerView.setItemViewCacheSize(10)
        binding.selectionRecyclerView.setHasFixedSize(true)
        binding.selectionRecyclerView.adapter=adapter
        binding.selectionRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.backFromSelection.setOnClickListener{
            findNavController().navigate(SelectionFragmentDirections.actionSelectionFragmentToPlayListSongsFragment(position))
        }
        return binding.root
    }

    private fun checkSong(song: SongLayoutModel): Boolean {
        MusicList.musicList[PlayListSongsFragment.position].songsList.forEachIndexed { index, songLayoutModel ->
            if(song.id==songLayoutModel.id)
            {
                MusicList.musicList[PlayListSongsFragment.position].songsList.removeAt(index)
                return false
            }
        }

        MusicList.musicList[PlayListSongsFragment.position].songsList.add(song)
        return true
    }


}