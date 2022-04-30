package com.example.mividplayer.fragments.playsong

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentNowPlayingBinding
import com.example.mividplayer.fragments.home.MainViewFragmentDirections
import com.example.mividplayer.models.SongLayoutModel


class NowPlayingFragment : Fragment()
{
    companion object{
        lateinit var binding:FragmentNowPlayingBinding
        var isresumed=false
        val listen : MutableLiveData<Int> =  MutableLiveData()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_now_playing,container,false)
        binding.root.visibility=View.GONE
        binding.pausePlay.setOnClickListener{
            if(SongPlayingFragment.isplaying)
            {
                pausemusic()
            }
            else
            {
                playmusic()
            }
        }


        listen.setValue(SongPlayingFragment.index) //Initilize with a value

        listen.observe(viewLifecycleOwner, Observer {

            if(isresumed)
            {
                Glide.with(requireContext()).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
                    .apply(RequestOptions.placeholderOf(R.drawable.music))
                    .into(binding.songImg)
            }


        })
        binding.playNext.setOnClickListener{
            nextprev(true)
        }
        binding.playPrevious.setOnClickListener{
            nextprev(false)
        }
        binding.root.setOnClickListener{
            findNavController().navigate(MainViewFragmentDirections.actionMainViewFragmentToSongPlayingFragment(SongPlayingFragment.index.toString(),"NowPlaying"))
        }
        return binding.root
    }

    override fun onResume() {
        if(SongPlayingFragment.musicService!=null)
        {
            isresumed=true
            binding.root.visibility=View.VISIBLE
            binding.nowPlayingSong.isSelected=true

            Glide.with(requireContext()).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
                .apply(RequestOptions.placeholderOf(R.drawable.music))
                .into(binding.songImg)
            binding.nowPlayingSong.text=SongPlayingFragment.songsList[SongPlayingFragment.index].songName
            if(SongPlayingFragment.isplaying==true)
                binding.pausePlay.setImageResource(R.drawable.ic_pause)
            else
            {
                binding.pausePlay.setImageResource(R.drawable.ic_play_arrow)
            }
        }
        super.onResume()
    }
    private fun playmusic()
    {
        SongPlayingFragment.isplaying=true
        SongPlayingFragment.musicService!!.mediaPlayer!!.start()
        binding.pausePlay.setImageResource(R.drawable.ic_pause)
        changePlayPuaseIcon(true)
        SongPlayingFragment.musicService!!.shownotification(R.drawable.ic_pause,1F)
    }
    private fun pausemusic()
    {
        SongPlayingFragment.isplaying=false
        SongPlayingFragment.musicService!!.mediaPlayer!!.pause()
        binding.pausePlay.setImageResource(R.drawable.ic_play_arrow)

        changePlayPuaseIcon(false)
        SongPlayingFragment.musicService!!.shownotification(R.drawable.ic_play_arrow,0F)
    }
    fun nextprev(increment:Boolean)
    {
        SongLayoutModel.setposition(increment)
        SongPlayingFragment.musicService!!.initializeMusic()
        Glide.with(requireContext()).load(SongPlayingFragment.songsList[SongPlayingFragment.index].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(binding.songImg)
        binding.nowPlayingSong.text=SongPlayingFragment.songsList[SongPlayingFragment.index].songName
        SongPlayingFragment.binding.currentPlayingSongName.text = SongPlayingFragment.songsList[SongPlayingFragment.index].songName
        SongPlayingFragment.binding.currentPlayingSingerName.text = SongPlayingFragment.songsList[SongPlayingFragment.index].artist
        SongPlayingFragment.binding.currentPlayingEndTime.text = SongLayoutModel
            .getduration(SongPlayingFragment.songsList[SongPlayingFragment.index].duration)
        playmusic()


    }
    fun changePlayPuaseIcon(sigal:Boolean)
    {
        if(sigal)
        {
            SongPlayingFragment.binding.pauseBtn.visibility=View.GONE
            SongPlayingFragment.binding.playBtn.visibility=View.VISIBLE
        }
        else
        {
            SongPlayingFragment.binding.playBtn.visibility=View.GONE
            SongPlayingFragment.binding.pauseBtn.visibility=View.VISIBLE
        }
    }

}