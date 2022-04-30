package com.example.mividplayer.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R

import com.example.mividplayer.databinding.PlaylistViewBinding
import com.example.mividplayer.fragments.music.PlayListSongsFragment
import com.example.mividplayer.models.MusicList
import com.example.mividplayer.models.Playlist


class PlayLIstAdapter(val context: Context):
    RecyclerView.Adapter<PlayLIstAdapter.PlayListViewHolder>() {
    val musicplaylist:ArrayList<Playlist> =ArrayList()
    private lateinit var mplayListItemClickListener:PlayListItemClickListener
    interface PlayListItemClickListener{
      fun ontiemClick(position: Int,view:PlaylistViewBinding)
      fun onDeleteClick(position: Int)



    }
    fun initPlayListItemClickListener(playListItemClickListener: PlayListItemClickListener)
    {
        mplayListItemClickListener=playListItemClickListener
    }
    class PlayListViewHolder(playlistViewBinding: PlaylistViewBinding,playListItemClickListener: PlayListItemClickListener):RecyclerView.ViewHolder(playlistViewBinding.root){
        val image=playlistViewBinding.playlistImage
        val playlistName=playlistViewBinding.playlistName
        init {
            playlistViewBinding.deletePlaylist.setOnClickListener{
                playListItemClickListener.onDeleteClick(adapterPosition)
            }
            playlistViewBinding.playlistImage.setOnClickListener{
                playListItemClickListener.ontiemClick(adapterPosition,playlistViewBinding)
            }


        }
    }
//    var playListDiffer= object: DiffUtil.ItemCallback<Playlist>() {
//        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
//          return  oldItem.playlistName==newItem.playlistName
//        }
//
//        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
//           return oldItem.equals(newItem)
//        }
//
//
//    }
//    private var playlistAsyncListDiffer= AsyncListDiffer(this,playListDiffer)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        return PlayListViewHolder(PlaylistViewBinding.inflate(LayoutInflater.from(parent.context),parent,false),mplayListItemClickListener)
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        val currsong= musicplaylist[position]
        holder.playlistName.text=currsong.playlistName
        holder.playlistName.isSelected=true
        if(MusicList.musicList.get(position).songsList.size>0)
        {
            Glide.with(context).load(MusicList.musicList.get(position).songsList[0].uri)
                .apply(RequestOptions.placeholderOf(R.drawable.music))
                .into(holder.image)
        }

    }

    override fun getItemCount(): Int {
        return musicplaylist.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(playlist:ArrayList<Playlist>)
    {
        musicplaylist.clear()
        musicplaylist.addAll(playlist)
        notifyDataSetChanged()

    }
}