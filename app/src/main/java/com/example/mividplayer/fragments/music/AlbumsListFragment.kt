package com.example.mividplayer.fragments.music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.adapters.SongsAdapter
import com.example.mividplayer.databinding.FragmentAlbumsListBinding
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.fragments.home.AlbumsFragment


class AlbumsListFragment : Fragment() {


    lateinit var binding:FragmentAlbumsListBinding
    private val albumArgs by navArgs<AlbumsListFragmentArgs>()
    lateinit var adapter : SongsAdapter
    companion object{
        var index=-1

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_albums_list,container,false)
        index=Integer.parseInt(albumArgs.index)
        binding.albumTitle.text= AlbumsFragment.albumsList[index].albumName
        Glide.with(requireContext()).load(AlbumsFragment.albumsList[index].albumSongs[0].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music).centerCrop())
            .into(binding.albumImage)

        adapter= SongsAdapter(requireContext())
        adapter.setsData(AlbumsFragment.albumsList[index].albumSongs)
        adapter.initItemClickListener(object :SongsAdapter.onItemClickListener{
            override fun onitemClick(position: Int, view: SongLayoutBinding) {
              findNavController().navigate(AlbumsListFragmentDirections.actionAlbumsListFragmentToSongPlayingFragment(position.toString(),"AlbumPlaylist"))
               }

        })
        binding.albumSongsRecyclerView.setHasFixedSize(true)
        binding.albumSongsRecyclerView.adapter=adapter
        binding.albumSongsRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.backFromAlbumList.setOnClickListener{
            findNavController().navigate(AlbumsListFragmentDirections.actionAlbumsListFragmentToMainViewFragment("3"))
        }
        return binding.root
    }


}