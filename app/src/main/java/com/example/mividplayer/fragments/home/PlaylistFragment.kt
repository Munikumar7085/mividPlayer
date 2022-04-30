package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.adapters.PlayLIstAdapter
import com.example.mividplayer.databinding.CustomDialogBinding
import com.example.mividplayer.databinding.FragmentPlaylistBinding
import com.example.mividplayer.databinding.PlaylistViewBinding
import com.example.mividplayer.fragments.music.PlayListSongsFragment
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.Playlist
import com.example.mividplayer.models.SongLayoutModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PlaylistFragment : Fragment() {
    private lateinit var adapter:PlayLIstAdapter
   private lateinit var binding:FragmentPlaylistBinding
   companion object{
       var playlistSearch=false
   }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_playlist,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.playlistHomeToolbar)
        binding.playlistHomeToolbar.title="Playlists"
        setHasOptionsMenu(true)

         adapter=PlayLIstAdapter(requireContext())
        adapter.initPlayListItemClickListener(object :PlayLIstAdapter.PlayListItemClickListener{
            override fun ontiemClick(position: Int, view: PlaylistViewBinding) {
                findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToPlayListSongsFragment(position.toString()))

            }


            @SuppressLint("NotifyDataSetChanged")
            override fun onDeleteClick(position: Int) {
                val alretbuilder= context?.let { AlertDialog.Builder(it) }
                if (alretbuilder != null) {
                    alretbuilder.setTitle(MusicList.musicList[position].playlistName)
                        .setMessage("Do you want to Delete?")
                        .setPositiveButton("Yes"){_,_->
                            MusicList.musicList.removeAt(position)
                            adapter.setData(MusicList.musicList)
                            adapter.notifyDataSetChanged()
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

        })
        adapter.setData(MusicList.musicList)
        binding.playListRecyclerView.adapter=adapter
        binding.playListRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        binding.addPlayList.setOnClickListener{
            customeAlertDialog(container)
        }
        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mymenu,menu)
        val searchView=menu.findItem(R.id.search_songs).actionView as SearchView
        val txtSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Search playlist..."
        txtSearch.setHintTextColor(Color.rgb(109,129,161))
        txtSearch.setTextColor(Color.rgb(4,30,31))
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null)
                {
                    val playlist=ArrayList<Playlist>()
                    val searchText= newText.lowercase(Locale.getDefault())
                    for(list in MusicList.musicList)
                    {
                        if(list.playlistName.lowercase(Locale.getDefault()).contains(searchText))
                        {
                            playlist.add(list)
                        }
                    }
                    playlistSearch =true
                    adapter.setData(playlist)
                    adapter.notifyDataSetChanged()

                }
                //Toast.makeText(requireContext(), newText,Toast.LENGTH_SHORT).show()
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun customeAlertDialog(container: ViewGroup?)
    {
        val customDialog=LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog,container,false)
        val customBind=CustomDialogBinding.bind(customDialog)
        val dialog=MaterialAlertDialogBuilder(requireContext())
        dialog.setView(customDialog)
        dialog.setTitle("PlayList Details")
        dialog.setPositiveButton("ADD"){alert,_->
            val playlistName=customBind.playListNameEdit.text
            val creatorName=customBind.playListCreatorNameEdit.text
            if(playlistName!=null&&creatorName!=null)
            {
                if(playlistName.isNotEmpty()&&creatorName.isNotEmpty())
                {
                    addPlaylist(playlistName,creatorName)
                }
            }
           alert.dismiss()
        }.show()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addPlaylist(playlistName: Editable, creatorName: Editable) {
        var isadded=false
        for( i in MusicList.musicList)
        {
            if(playlistName.toString().equals(i.playlistName))
            {
                isadded=true
                break
            }
        }
        if(isadded)
        {
            Toast.makeText(requireContext(),"PlayList is already Exists",Toast.LENGTH_SHORT).show()

        }
        else
        {
            val playlist= Playlist()
            playlist.playlistName=playlistName.toString()
            playlist.creator=creatorName.toString()
            playlist.songsList= ArrayList()
            val calendar=Calendar.getInstance().time
            val sdf=SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            playlist.createdOn=sdf.format(calendar)
            MusicList.musicList.add(playlist)
            adapter.setData(MusicList.musicList)
            adapter.notifyDataSetChanged()

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
     adapter.notifyDataSetChanged()
        super.onResume()
    }
}


