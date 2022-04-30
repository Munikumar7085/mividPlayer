package com.example.mividplayer.fragments.startupslides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentSharePlaylistBinding


class SharePlaylistFragment : Fragment() {


    private lateinit var binding:FragmentSharePlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_share_playlist,container,false)
        binding.getStart.setOnClickListener{
            findNavController().navigate(SharePlaylistFragmentDirections.actionSharePlaylistFragmentToSignInFragment())
        }
        return binding.root
    }



}