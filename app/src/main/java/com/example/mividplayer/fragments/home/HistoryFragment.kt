package com.example.mividplayer.fragments.home


import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.SongsAdapter
import com.example.mividplayer.databinding.FragmentHistoryBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.fragments.playsong.SongPlayingFragment
import com.example.mividplayer.models.SongLayoutModel
import java.util.*
import kotlin.collections.ArrayList


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    companion object{
        lateinit var historySearchList:ArrayList<SongLayoutModel>
        var songSearch=false
        var historyList:ArrayList<SongLayoutModel> = ArrayList()
        lateinit var  adapter: SongsAdapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_history,container,false)
        songSearch =false
        (activity as AppCompatActivity).setSupportActionBar(binding.songHistoryToolbar)
        setHasOptionsMenu(true)
        binding.songHistoryToolbar.title="History"
        if(!songSearch)
        {
            binding.totalSongs.text="Total songs ${historyList.size}"
        }

        adapter =SongsAdapter(requireContext())
        adapter.setsData(historyList)
        binding.historyRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter= adapter
        adapter.initItemClickListener(object :SongsAdapter.onItemClickListener{


            override fun onitemClick(position: Int, view: SongLayoutBinding) {
                var send= "HistoryAdapter"

                if(SongsFragment.songSearch)
                {
                    send="HistorySearchList"
                }
                if(SongsFragment.songsCollection[position].id== SongPlayingFragment.songId)
                {
                    send="NowPlaying"
                }
                findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToSongPlayingFragment(position.toString(),send))

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
                   historySearchList =ArrayList()
                    val searchText= newText.lowercase(Locale.getDefault())
                    for(song in historyList)
                    {
                        if(song.songName.lowercase(Locale.getDefault()).contains(searchText))
                        {
                            historySearchList.add(song)
                        }
                    }
                    songSearch =true
                   adapter.setsData(historySearchList)
                    binding.totalSongs.text= "Total songs ${historySearchList.size}"

                }
                //Toast.makeText(requireContext(), newText,Toast.LENGTH_SHORT).show()
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }


}