@file:Suppress("DEPRECATION")

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentSongPlayingBinding
import com.example.mividplayer.fragments.home.*
import com.example.mividplayer.fragments.music.AlbumsListFragment
import com.example.mividplayer.fragments.music.PlayListSongsFragment
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.SongLayoutModel
import com.example.mividplayer.services.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialog



class SongPlayingFragment : Fragment(),ServiceConnection,MediaPlayer.OnCompletionListener{
    private val args by navArgs<SongPlayingFragmentArgs>()
    companion object{
        lateinit var songsList:ArrayList<SongLayoutModel>
        var isPlaying =false
        var index: Int = 0
        var musicService:MusicService?=null
        var loopCount=0
        lateinit var binding:FragmentSongPlayingBinding
        var backFragment=0
        var min15=false
        var min30=false
        var min60=false
        var songId=""
        var isFavorite=false
        var favIndex=-1
        var startPlay=true
        var nowPlayingList=ArrayList<SongLayoutModel>()


    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_song_playing,container,false)
        setLooper(false)
        binding.backFromCurrentPlaying.setOnClickListener {

            if (backFragment == 5) {
                findNavController().navigate(SongPlayingFragmentDirections.actionSongPlayingFragmentToHistoryFragment())
            } else
            {
                findNavController().navigate(SongPlayingFragmentDirections.actionSongPlayingFragmentToMainViewFragment(
                    backFragment.toString()))
            }



        }

        binding.playingEquilizer.setOnClickListener{
            try {
                val equalizer=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                equalizer.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                equalizer.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,requireActivity().baseContext.packageName)
                equalizer.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(equalizer,7)
            }
            catch (e:Exception)
            {
                Toast.makeText(requireContext(),"${e.message}",Toast.LENGTH_SHORT).show()
            }

        }
        binding.repeatLayout.setOnClickListener{
            setLooper(true)
        }
        binding.addFavorite.setOnClickListener{
            if(isFavorite)
            {
                isFavorite=false
                changeFavIcon()

                FavoriteFragment.favoriteSongsList.removeAt(favIndex)
                favIndex--
            }
            else{
                isFavorite=true
                changeFavIcon()
                FavoriteFragment.favoriteSongsList.add(songsList[index])
                favIndex++
            }
        }
        binding.palyPauseCard.setOnClickListener{
            if(musicService!!.mediaPlayer!=null)
            {
                if(!startPlay && index==0)
                {
                    startPlay=true
                }
                if(isPlaying)
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
            playNextPrev(true)
        }
        binding.previousSong.setOnClickListener{
            playNextPrev(false)
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
                timerReset()

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

    private fun timerReset() {
        val alretbuilder = context?.let { AlertDialog.Builder(it) }
        alretbuilder?.setTitle("Stop Timer")?.setMessage("Do you want to stop timer?")
            ?.setPositiveButton("Yes") { _, _ ->

                min15 = false
                min30 = false
                min60 = false
                binding.timerCardOn.visibility = View.GONE
                binding.timerCardOff.visibility = View.VISIBLE

            }?.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        alretbuilder?.create()?.show()


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
                Thread.sleep((15*60000).toLong())
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
                Thread.sleep((30*60000).toLong())
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
                Thread.sleep((60*60000).toLong())
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
        if(isFavorite)
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

    private fun changeRepeaticon(scenario:Int)
    {
        when(scenario)
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
    private fun setLooper(pressed:Boolean) {

        if(pressed)
        {
            loopCount= (loopCount+1)%3

        }
        changeRepeaticon(loopCount)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun playNextPrev(increment: Boolean) {
        SongLayoutModel.setPosition(increment)
        Toast.makeText(requireContext(),"song position : $index",Toast.LENGTH_SHORT).show()
        if(loopCount==0&& index== songsList.size-1)
        {
            startPlay=false
        }
            isPlaying=true
            changePlayPauseIcon(isPlaying)
            updateNowPlaying()
            startMusic()
        if(index==0 && !startPlay&& loopCount==0)
        {
            pauseMusic()
        }

    }
    private fun updateNowPlaying()
    {
        NowPlayingFragment.binding.nowPlayingSong.text= songsList[index].songName


    }



    private fun playMusic() {
        isPlaying=true
        changePlayPauseIcon(isPlaying)
        musicService!!.showNotification(R.drawable.ic_pause,1F)
        musicService!!.mediaPlayer!!.start()

    }

    private fun pauseMusic() {
        isPlaying=false
        changePlayPauseIcon(isPlaying)
        musicService!!.showNotification(R.drawable.ic_play_arrow,0F)
        musicService!!.mediaPlayer!!.pause()

    }
    private fun changePlayPauseIcon(signal:Boolean)
    {
        if(signal)
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
                songsList.addAll(AlbumsFragment.albumsList[AlbumsListFragment.index].albumSongs)
                startMusicService()
                backFragment=3
                initializeMusic()

                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "PlaylistFragment"->
            {
                songsList= ArrayList()
                songsList.addAll(MusicList.musicList[PlayListSongsFragment.position].songsList)
                startMusicService()
                backFragment=4
                initializeMusic()

                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "FavoriteAdapter"->
            {

                songsList= ArrayList()
                songsList.addAll(FavoriteFragment.favoriteSongsList)
                startMusicService()
                backFragment=2
                initializeMusic()
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
            }
            "NowPlaying"->
            {
                checkFav()
                songsList= ArrayList()
                songsList.addAll(nowPlayingList)
                loadImage()
                setLooper(false)
                setSongName()
                seekBarLayout(musicService!!.mediaPlayer!!.currentPosition)
                changePlayPauseIcon(isPlaying)

            }
            "SearchList"->
            {

                songsList= ArrayList()
                songsList.addAll(SongsFragment.songSearchList)
                startMusicService()
                backFragment=1
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)

                initializeMusic()

            }
            "SongsAdapter"->
            {
                songsList= ArrayList()
                songsList.addAll(HomeFragment.songsCollection)
                startMusicService()
                backFragment=1
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "favoriteShuffle"->
            {
                songsList= ArrayList()
                songsList.addAll(FavoriteFragment.favoriteSongsList)
                startMusicService()
                backFragment=2
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "Homeshuffle"->
            {
                songsList= ArrayList()
                songsList.addAll(HomeFragment.songsCollection)
                startMusicService()
                backFragment=0
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "HistoryAdapter"->
            {
                songsList= ArrayList()
                songsList.addAll(HistoryFragment.historyList)
                startMusicService()
                backFragment=5
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()
            }
            "HistorySearchList"->
            {

                songsList= ArrayList()
                songsList.addAll(HistoryFragment.historySearchList)
                startMusicService()
                backFragment=5
                nowPlayingList.clear()
                nowPlayingList.addAll(songsList)
                initializeMusic()

            }

        }
        if(loopCount==0&& index== songsList.size-1)
        {
            startPlay=false
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
        if(musicService!=null)
        {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }
            startMusic()
        }



    }



    private fun loadImage()
    {

        Glide.with(requireContext()).load(songsList[index].uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music))
            .into(binding.currentPlayingImg)



    }
    private fun setSongName()
    {
        binding.currentPlayingSongName.text = songsList[index].songName
        binding.currentPlayingSingerName.text = songsList[index].artist
    }
    private fun seekBarLayout(start:Int)
    {
        binding.currentPlayingStartTime.text=SongLayoutModel.getDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.currentSeekbar.progress=start
        binding.currentSeekbar.max= musicService!!.mediaPlayer!!.duration
        binding.currentPlayingEndTime.text = SongLayoutModel
            .getDuration(songsList[index].duration)
    }
    private fun checkFav()
    {
        favIndex=SongLayoutModel.isFavoriteChecker(songsList[index].id)
        changeFavIcon()
    }
    private fun setLayout(playPauseIcon:Int)
    {

        setSongName()

        checkFav()
        isPlaying = true
        seekBarLayout(0)
        musicService!!.showNotification(playPauseIcon,1F)


    }
    private fun preparePlayer()
    {
        musicService!!.mediaPlayer!!.reset()
        try {
            musicService!!.mediaPlayer!!.setDataSource(songsList[index].path)
        }
        catch (e:Exception)
        {
            Log.i("setData","${e.message}")
        }
        musicService!!.mediaPlayer!!.prepare()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun startMusic()
    {
        loadImage()
        preparePlayer()
        musicService!!.mediaPlayer!!.start()
        setLayout(R.drawable.ic_pause)
        musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        songId= songsList[index].id
        if(HistoryFragment.historyList.isEmpty()|| HistoryFragment.historyList[HistoryFragment.historyList.size-1].id!= songId)
        {
            HistoryFragment.historyList.add(songsList[index])
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder= service as MusicService.MyBinder
        musicService=myBinder.getService()
        initializeMusic()
        musicService!!.startRunnable()
        musicService!!.audioManager=(activity as AppCompatActivity).getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCompletion(mp: MediaPlayer?) {
        if(loopCount==2)
       {
           startMusic()
       }
        else
       {

               playNextPrev(true)


       }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==7||resultCode==RESULT_OK)
            return
    }
}


