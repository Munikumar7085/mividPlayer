package com.example.mividplayer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mividplayer.R
import com.example.mividplayer.databinding.SongLayoutBinding
import com.example.mividplayer.models.SongLayoutModel

class SongsAdapter(val context:Context): RecyclerView.Adapter<SongsAdapter.holder>() {
    private lateinit var mitemClickListener: onItemClickListener
    interface onItemClickListener{
        fun onitemClick(position: Int,view: SongLayoutBinding)
    }
    fun initItemClickListener(itemClickListener: onItemClickListener)
    {
        mitemClickListener=itemClickListener
    }
    class holder(songLayoutBinding: SongLayoutBinding,itemClickListener: onItemClickListener):RecyclerView.ViewHolder(songLayoutBinding.root)
    {
        val songName=songLayoutBinding.songName
        val albumName=songLayoutBinding.songAlbum
        val songImg=songLayoutBinding.songImg
        val duration=songLayoutBinding.duration
        init {
            songLayoutBinding.root.setOnClickListener{
                itemClickListener.onitemClick(adapterPosition,songLayoutBinding)
            }
        }
    }
    private val listDiffer= object : DiffUtil.ItemCallback<SongLayoutModel>() {
        override fun areItemsTheSame(oldItem: SongLayoutModel, newItem: SongLayoutModel): Boolean {
            return oldItem.songName==newItem.songName
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: SongLayoutModel,
            newItem: SongLayoutModel
        ): Boolean {
           return oldItem == newItem
        }

    }
    private var asyncListDiffer=AsyncListDiffer(this,listDiffer)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {

        return holder(SongLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),mitemClickListener)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
       val currsong=asyncListDiffer.currentList.get(position)

        Glide.with(context).load(currsong.uri)
            .apply(RequestOptions.placeholderOf(R.drawable.music).centerCrop())
            .into(holder.songImg)
        holder.songName.text=currsong.songName
        holder.albumName.text=currsong.albumName
        holder.duration.text=SongLayoutModel.getduration(currsong.duration)

    }

    override fun getItemCount(): Int {
         return asyncListDiffer.currentList.size
    }
    fun setsData(songLayoutModel: List<SongLayoutModel>)
    {
        asyncListDiffer.submitList(songLayoutModel)
    }
}