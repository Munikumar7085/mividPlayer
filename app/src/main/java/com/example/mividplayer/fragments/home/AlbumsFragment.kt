package com.example.mividplayer.fragments.home


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.AlbumAdapter
import com.example.mividplayer.databinding.FragmentAlbumsBinding
import com.example.mividplayer.fragments.music.AlbumCreator
import com.example.mividplayer.models.AlbumModel
import kotlin.collections.ArrayList


class AlbumsFragment : Fragment() {

 lateinit var binding:FragmentAlbumsBinding
 companion object{
      var albumsList:ArrayList<AlbumModel> = ArrayList()
     lateinit var adapter: AlbumAdapter
      var isLayoutSet=false
 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_albums,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.albumsHomeToolbar)
        binding.albumsHomeToolbar.title="Albums"
        if(!isLayoutSet)
        {
            AlbumCreator.setAlbum()
        }

        adapter=AlbumAdapter(requireContext())
        adapter.setsData(albumsList)
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


}