package com.example.mividplayer.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.AlbumAdapter
import com.example.mividplayer.databinding.FragmentAlbumsBinding
import com.example.mividplayer.models.AlbumModel


class AlbumsFragment : Fragment() {

 lateinit var binding:FragmentAlbumsBinding
 companion object{
      var albumsList:ArrayList<AlbumModel> = ArrayList()
      var islayoutset=false
 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_albums,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.albumsHomeToolbar)
        binding.albumsHomeToolbar.title="Albums"
        setHasOptionsMenu(true)
        if(!islayoutset)
        setAlbum()
        val adapter=AlbumAdapter(requireContext())
        adapter.sestData(albumsList)
        adapter.initItemClickListener(object :AlbumAdapter.onAlbumClickListener{
            override fun onitemClick(position: Int) {
               findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToAlbumsListFragment(position.toString()))
                }

        })
        binding.AlbumRecyclerView.setHasFixedSize(true)
        binding.AlbumRecyclerView.adapter=adapter
        binding.AlbumRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        "Total Albums : ${albumsList.size}".also { binding.totalAlbums.text = it }

        return binding.root
    }

    private fun setAlbum() {
        var present: Boolean
        var presentIndex=0
        HomeFragment.songsCollection.forEachIndexed { index, songLayoutModel ->
            present=false
            albumsList.forEachIndexed { index, albumModel ->
               if(albumModel.albumName.equals(songLayoutModel.albumName))
               {
                   present=true
                   presentIndex=index
               }

            }
            if(present)
            {
                if(!albumsList.get(presentIndex).albumSongs.contains(songLayoutModel))
                 albumsList.get(presentIndex).albumSongs.add(songLayoutModel)
            }
            else
            {
                val album=AlbumModel()
                album.albumName=songLayoutModel.albumName
                if(!album.albumSongs.contains(songLayoutModel))
                 album.albumSongs.add(songLayoutModel)
                albumsList.add(album)
            }
        }
        islayoutset=true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }


}