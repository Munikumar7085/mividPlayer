package com.example.mividplayer.fragments.music

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.adapters.SongsAdapter
import com.example.mividplayer.databinding.FragmentPlayListSongsBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.SongLayoutModel

class PlayListSongsFragment : Fragment() {


   private val args by navArgs<PlayListSongsFragmentArgs>()
    lateinit var binding:FragmentPlayListSongsBinding
    lateinit var adapter:SongsAdapter

    companion object
    {
        var position=-1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_play_list_songs,container,false)
        position=Integer.parseInt(args.position)
        try {

            MusicList.musicList[position].songsList=SongLayoutModel
                .checkSongExist(MusicList.musicList[position].songsList)
        }
        catch (e:Exception)
        {
            Toast.makeText(requireContext(),"${e.message}",Toast.LENGTH_SHORT).show()
        }

        adapter= SongsAdapter(requireContext())
        adapter.setsData(MusicList.musicList[position].songsList)
        adapter.initItemClickListener(object :SongsAdapter.onItemClickListener{


            override fun onitemClick(position: Int, view: SongLayoutBinding) {
                findNavController().navigate(PlayListSongsFragmentDirections.actionPlayListSongsFragmentToSongPlayingFragment(position.toString(),"PlaylistFragment"))

            }

        })
        binding.playlistTitle.text= MusicList.musicList[position].playlistName
        binding.currentPlaylistRecyclerView.setHasFixedSize(true)
        binding.currentPlaylistRecyclerView.adapter=adapter
        binding.currentPlaylistRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.addSongs.setOnClickListener{
            findNavController().navigate(PlayListSongsFragmentDirections.actionPlayListSongsFragmentToSelectionFragment(
                position.toString()))
        }
        binding.backFromCurrentPlaylist.setOnClickListener{
            findNavController().navigate(PlayListSongsFragmentDirections.actionPlayListSongsFragmentToMainViewFragment("4"))
        }
        binding.removeAll.setOnClickListener{
            MusicList.musicList[position].songsList.clear()
            binding.playlistHeadImage.setImageResource(R.drawable.music)
            binding.createdOn.text=""
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        if(adapter.itemCount>0)
        {
            binding.createdOn.text="Total ${adapter.itemCount} songs\n\n" +
                    "Created on:\n${MusicList.musicList[position].createdOn}\n\n"+
                    MusicList.musicList[position].creator
            Glide.with(requireContext()).load(MusicList.musicList[position].songsList[0].uri)
                .apply(RequestOptions.placeholderOf(R.drawable.music))
                .into(binding.playlistHeadImage)

        }
        super.onResume()
    }

}