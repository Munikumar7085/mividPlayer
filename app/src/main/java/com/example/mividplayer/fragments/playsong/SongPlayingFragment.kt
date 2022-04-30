package com.example.mividplayer.fragments.playsong



import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentSongPlayingBinding
import com.example.mividplayer.fragments.home.AlbumsFragment
import com.example.mividplayer.fragments.home.FavoriteFragment
import com.example.mividplayer.fragments.home.HomeFragment
import com.example.mividplayer.fragments.home.SongsFragment
import com.example.mividplayer.fragments.music.AlbumsListFragment
import com.example.mividplayer.fragments.music.PlayListSongsFragment
import com.example.mividplayer.models.MusicList

import com.example.mividplayer.models.SongLayoutModel
import com.example.mividplayer.services.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.Appendable


class SongPlayingFragment : Fragment(),ServiceConnection,MediaPlayer.OnCompletionListener{

    private val TaG="SongPlaying"


    private val args by navArgs<SongPlayingFragmentArgs>()
    companion object{
        lateinit var songsList:ArrayList<SongLayoutModel>
        var isplaying=false
        var index: Int = 0
        var musicService:MusicService?=null
        var loopCount=0
        lateinit var binding:FragmentSongPlayingBinding
        var backfragment=0
        var min15=false
        var min30=false
        var min60=false
        var songId=""
        var isfavorite=false
        var favIndex=-1
        var nowPlayingList=ArrayList<SongLayoutModel>()


    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_song_playing,container,false)
        setlooper(false)
        binding.backFromCurrentPlaying.setOnClickListener{

            findNavController().navigate(SongPlayingFragmentDirections.actionSongPlayingFragmentToMainViewFragment(
                backfragment.toString()))


        }

        binding.playingEquilizer.setOnClickListener{
            try {
                val equlizer=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                equlizer.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                equlizer.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,requireActivity().baseContext.packageName)
                equlizer.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(equlizer,7)
            }
            catch (e:Exception)
            {
                Toast.makeText(requireContext(),"${e.message}",Toast.LENGTH_SHORT).show()
                Log.i(TaG,"excperion : ${e.message}")
            }

        }
        binding.repeatLayout.setOnClickListener{
            setlooper(true)
        }
        binding.addFavorite.setOnClickListener{
            if(isfavorite)
            {
                isfavorite=false
                changeFavIcon()
                FavoriteFragment.favoriteSongsList.removeAt(favIndex)
            }
            else{
                isfavorite=true
                changeFavIcon()
                FavoriteFragment.favoriteSongsList.add(songsList[index])
            }
        }
        binding.palyPauseCard.setOnClickListener{
            if(musicService!!.mediaPlayer!=null)
            {
                if(isplaying)
                {
                    pauseMusic()
                }
                else
                {
                    playMusic()
                }
            }

        }
        binding.currentSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                {
                    musicService!!.mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                return
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                return
            }

        })
        binding.nextSong.setOnClickListener{
            playnextprev(true)
        }
        binding.previousSong.setOnClickListener{
            playnextprev(false)
        }
        if(min15||min30||min60)
        {
            changeTimerIcon()
        }

        binding.timerLayout.setOnClickListener{
            val timer=(min15|| min30|| min60)
            if(!timer)
                showBottomSheet()
            else
            {
                TimerReset()

            }
        }
        binding.shareSong.setOnClickListener{
            val shareSong=Intent()
            shareSong.action=Intent.ACTION_SEND
            shareSong.type="audio/*"
            shareSong.putExtra(Intent.EXTRA_STREAM, Uri.parse(songsList[index].path))
            startActivity(Intent.createChooser(shareSong, songsList[index].songName))


        }
        checkClass()
        return binding.root
    }

    fun TimerReset()
    {
        val alretbuilder= context?.let { AlertDialog.Builder(it) }
        if (alretbuilder != null) {
            alretbuilder.setTitle("Stop Timer")
                .setMessage("Do you want to stop timer?")
                .setPositiveButton("Yes"){_,_->

                    min15=false
                    min30=false
                    min60=false
                    binding.timerCardOn.visibility=View.GONE
                    binding.timerCardOff.visibility=View.VISIBLE

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
    private fun showBottomSheet() {
        val timerSheet=BottomSheetDialog(requireContext())
        timerSheet.setContentView(R.layout.timer_bottom_sheet)
        timerSheet.show()
        timerSheet.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(requireContext(),"Mivid Player will be close in 15 minutes",Toast.LENGTH_SHORT).show()
            min15=true
            changeTimerIcon()
            timerSheet.dismiss()
            Thread{
                Thread.sleep(15*60000)
                if(min15)
                    SongLayoutModel.exitApplication()
            }.start()


        }
        timerSheet.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(requireContext(),"Mivid Player will be close in 30 minutes",Toast.LENGTH_SHORT).show()
            min30=true
            changeTimerIcon()
            timerSheet.dismiss()
            Thread{
                Thread.sleep(30*60000)
                if(min30)
                    SongLayoutModel.exitApplication()
            }.start()

        }
        timerSheet.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(requireContext(),"Mivid Player will be close in 60 minutes",Toast.LENGTH_SHORT).show()
            min60=true
            changeTimerIcon()
            timerSheet.dismiss()
            Thread{
                Thread.sleep(60*60000)
                if(min60)
                    SongLayoutModel.exitApplication()
            }.start()

        }
    }

    private fun changeTimerIcon() {
        binding.timerCardOff.visibility=View.GONE
        binding.timerCardOn.visibility=View.VISIBLE
    }





    private fun changeFavIcon() {
        if(isfavorite)
        {
            binding.favoriteOff.visibility=View.GONE
            binding.favoriteOn.visibility=View.VISIBLE
        }
        else
        {
            binding.favoriteOn.visibility=View.GONE
            binding.favoriteOff.visibility=View.VISIBLE
        }
    }

    fun changeRepeaticon(sinario:Int)
    {
        when(sinario)
        {
            0->
            {
                binding.repeatLoopOff.visibility = View.VISIBLE
                binding.repeatLoopOne.visibility=View.GONE
                binding.repeatLoopOn.visibility = View.GONE

            }
            1->
            {
                binding.repeatLoopOn.visibility = View.VISIBLE
                binding.repeatLoopOff.visibility=View.GONE
                binding.repeatLoopOne.visibility = View.GONE

            }
            2->
            {
                binding.repeatLoopOne.visibility = View.VISIBLE
                binding.repeatLoopOn.visibility=View.GONE
                binding.repeatLoopOff.visibility = View.GONE
            }
        }
    }
    private fun setlooper(pressed:Boolean) {

        if(pressed)
        {
            loopCount= (loopCount+1)%3

        }
        changeRepeaticon(loopCount)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun playnextprev(increment: Boolean) {
        SongLayoutModel.setposition(increment)
        Toast.makeText(requireContext(),"song position : $index",Toast.LENGTH_SHORT).show()
        Log.i(TaG,"song position $index and loopCount = $loopCount")
        if(loopCount==0&& index== songsList.size-1)
        {
            NowPlayingFragment.binding.pausePlay.setImageResource(R.drawable.ic_play_arrow)
            Log.i(TaG,"list ended index : $index")
            setLayout(R.drawable.ic_play_arrow)
            loadimage()
            preparePlayer()
            isplaying=false
            changePlayPuaseIcon(isplaying)
            updateNowPlaying()
        }
        else
        {
            isplaying=true
            changePlayPuaseIcon(isplaying)
            updateNowPlaying()
            startMusic()
        }

    }
    fun updateNowPlaying()
    {
        NowPlayingFragment.binding.nowPlayingSong.text= songsList[index].songName


    }



    private fun playMusic() {
        isplaying=true
        changePlayPuaseIcon(isplaying)
        musicService!!.shownotification(R.drawable.ic_pause,1F)
        musicService!!.mediaPlayer!!.start()

    }

    private fun pauseMusic() {
        isplaying=false
        changePlayPuaseIcon(isplaying)
        musicService!!.shownotification(R.drawable.ic_play_arrow,0F)
        musicService!!.mediaPlayer!!.pause()

    }
    fun changePlayPuaseIcon(sigal:Boolean)
    {
        if(sigal)
        {
            binding.pauseBtn.visibility=View.GONE
            binding.playBtn.visibility=View.VISIBLE
        }
        else
        {
            binding.playBtn.visibility=View.GONE
            binding.pauseBtn.visibility=View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkClass() {

        index=Integer.parseInt(args.index)


        when(args.address)
        {
            "AlbumPlaylist"->
            {
                songsList= ArrayList()
                songsList.addAll(AlbumsFragment.albumsList.get(AlbumsListFragment.index).albumSongs)
                startMusicService()
                backfragment=3
                initializeMusic()

                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "PlaylistFragment"->
            {
                songsList= ArrayList()
                songsList.addAll(MusicList.musicList.get(PlayListSongsFragment.position).songsList)
                startMusicService()
                backfragment=4
                initializeMusic()

                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "FavoriteAdapter"->
            {

                songsList= ArrayList()
                songsList.addAll(FavoriteFragment.favoriteSongsList)
                startMusicService()
                backfragment=2
                initializeMusic()
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "NowPlaying"->
            {
                checkfav()
                songsList= ArrayList()
                songsList.addAll(nowPlayingList)
                loadimage()
                setlooper(false)
                setsongName()
                SeekBarLayout(musicService!!.mediaPlayer!!.currentPosition)
                changePlayPuaseIcon(isplaying)

            }
            "SearchList"->
            {

                songsList= ArrayList()
                songsList.addAll(SongsFragment.songSearchList)
                startMusicService()
                backfragment=1
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)

                initializeMusic()

            }
            "SongsAdapter"->
            {
                songsList= ArrayList()
                songsList.addAll(HomeFragment.songsCollection)
                startMusicService()
                backfragment=1
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "favoriteShuffle"->
            {
                songsList= ArrayList()
                songsList.addAll(FavoriteFragment.favoriteSongsList)
                startMusicService()
                backfragment=2
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "Homeshuffle"->
            {
                songsList= ArrayList()
                songsList.addAll(HomeFragment.songsCollection)
                startMusicService()
                backfragment=0
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }

        }
    }
    private fun startMusicService()
    {
        val intent=Intent( requireContext(),MusicService::class.java)
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE)
        requireActivity().startService(intent)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMusic() {
        Log.i(TaG,"music service $musicService + ${Thread.currentThread().id}")
        if(musicService!=null)
        {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }
            startMusic()
        }



    }



    fun loadimage()
    {

        Glide.with(requireContext()).load(songsList[index].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(binding.currentPlayingImg)



    }
    fun setsongName()
    {
        binding.currentPlayingSongName.text = songsList[index].songName
        binding.currentPlayingSingerName.text = songsList[index].artist
    }
    fun SeekBarLayout(start:Int)
    {
        binding.currentPlayingStartTime.text=SongLayoutModel.getduration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.currentSeekbar.progress=start
        binding.currentSeekbar.max= musicService!!.mediaPlayer!!.duration
        binding.currentPlayingEndTime.text = SongLayoutModel
            .getduration(songsList[index].duration)
    }
    fun checkfav()
    {
        favIndex=SongLayoutModel.isFavoriteChecker(songsList[index].id)
        changeFavIcon()
    }
    fun setLayout(playpuseIcon:Int)
    {

        setsongName()

        checkfav()
        isplaying = true
        SeekBarLayout(0)
        musicService!!.shownotification(playpuseIcon,1F)

    }
    fun preparePlayer()
    {
        musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(songsList[index].path)
        musicService!!.mediaPlayer!!.prepare()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun startMusic()
    {
        loadimage()
        preparePlayer()
        musicService!!.mediaPlayer!!.start()
        setLayout(R.drawable.ic_pause)
        musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        songId= songsList[index].id
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val mybinder= service as MusicService.Mybinder
        Log.i(TaG,"mybinder $mybinder")
        musicService=mybinder.getService()
        initializeMusic()
        musicService!!.startRunnable()
        musicService!!.audioManager=(activity as AppCompatActivity).getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
        Log.i(TaG,"service connected $musicService")

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
        Log.i(TaG,"service disconnected $musicService")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCompletion(mp: MediaPlayer?) {

        //  Toast.makeText(requireContext(),"index : $index loop = $loop loopone= $loopone",Toast.LENGTH_SHORT).show()
       if(loopCount==2)
       {
           startMusic()
       }
        else
       {
           playnextprev(true)
       }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==7||resultCode==RESULT_OK)
            return
    }
}


