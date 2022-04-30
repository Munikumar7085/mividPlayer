package com.example.mividplayer.fragments.startupslides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentEnjoyMusicBinding


class EnjoyMusicFragment : Fragment() {


    companion object{
        var isSkipped=false
        var isClicked=false
    }
    private lateinit var binding:FragmentEnjoyMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_enjoy_music,container,false)
       binding.enjoyNext.setOnClickListener{
           findNavController().navigate(EnjoyMusicFragmentDirections.actionEnjoyMusicFragmentToNoAdsFragment())
       }
        binding.enjoySkip.setOnClickListener{
            isSkipped=true
            isClicked=true
            findNavController().navigate(EnjoyMusicFragmentDirections.actionEnjoyMusicFragmentToMainViewFragment("0"))
        }

        return binding.root
    }


}