package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.SongsAdapter
import com.example.mividplayer.databinding.FragmentSongsBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.models.SongLayoutModel
import java.util.*
import kotlin.collections.ArrayList


class SongsFragment : Fragment() {


    private lateinit var binding:FragmentSongsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    companion object{
        lateinit var songSearchList:ArrayList<SongLayoutModel>
        var songSearch=false
        lateinit var songsCollection:ArrayList<SongLayoutModel>
       @SuppressLint("StaticFieldLeak")
       lateinit var  adapter:SongsAdapter
    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_songs,container,false)
        songSearch=false
        (activity as AppCompatActivity).setSupportActionBar(binding.songHomeToolbar)
      setHasOptionsMenu(true)
        binding.songHomeToolbar.title="Songs"
        songsCollection= ArrayList()
        songsCollection=HomeFragment.songsCollection
        if(songSearch==false)
        {
            binding.totalSongs.text="Total songs ${songsCollection.size}"
        }

        adapter=SongsAdapter(requireContext())
        adapter.setsData(songsCollection)
        binding.songsRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.songsRecyclerView.adapter=adapter
        adapter.initItemClickListener(object :SongsAdapter.onItemClickListener{


            override fun onitemClick(position: Int, view: SongLayoutBinding) {
                var send= "SongsAdapter"

                if(songSearch)
                {
                    send="SearchList"
                }
                if(songsCollection[position].id==SongPlayingFragment.songId)
                {
                    send="NowPlaying"
                }
                findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment(position.toString(),send))

            }

        })

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu,menu)
        val searchView=menu.findItem(R.id.search_songs).actionView as SearchView
        val txtSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Search songs..."
        txtSearch.setHintTextColor(Color.rgb(109,129,161))
        txtSearch.setTextColor(Color.rgb(4,30,31))
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null)
                {
                    songSearchList=ArrayList()
                    val searchText= newText.lowercase(Locale.getDefault())
                   for(song in songsCollection)
                   {
                       if(song.songName.lowercase(Locale.getDefault()).contains(searchText))
                       {
                           songSearchList.add(song)
                       }
                   }
                    songSearch=true
                    adapter.setsData(songSearchList)
                    binding.totalSongs.text= "Total songs ${songSearchList.size}"

                }
               //Toast.makeText(requireContext(), newText,Toast.LENGTH_SHORT).show()
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

}