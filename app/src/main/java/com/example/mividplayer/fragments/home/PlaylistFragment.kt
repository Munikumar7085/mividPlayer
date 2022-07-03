package com.example.mividplayer.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mividplayer.R
import com.example.mividplayer.adapters.PlayLIstAdapter
import com.example.mividplayer.databinding.CustomDialogBinding
import com.example.mividplayer.databinding.FragmentPlaylistBinding
import com.example.mividplayer.databinding.PlaylistViewBinding
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PlaylistFragment : Fragment() {
    private lateinit var adapter:PlayLIstAdapter
   private lateinit var binding:FragmentPlaylistBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_playlist,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.playlistHomeToolbar)
        binding.playlistHomeToolbar.title="Playlists"

         adapter=PlayLIstAdapter(requireContext())
        adapter.initPlayListItemClickListener(object :PlayLIstAdapter.PlayListItemClickListener{
            override fun ontiemClick(position: Int, view: PlaylistViewBinding) {
                findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToPlayListSongsFragment(position.toString()))

            }


            @SuppressLint("NotifyDataSetChanged")
            override fun onDeleteClick(position: Int) {
                val alretbuilder = context?.let { AlertDialog.Builder(it) }
                alretbuilder?.setTitle(MusicList.musicList[position].playlistName)
                    ?.setMessage("Do you want to Delete?")
                    ?.setPositiveButton("Yes") { _, _ ->
                        MusicList.musicList.removeAt(position)
                        adapter.setData(MusicList.musicList)
                        adapter.notifyDataSetChanged()
                    }?.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                alretbuilder?.create()?.show()

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
            if(playlistName.toString() == i.playlistName)
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


